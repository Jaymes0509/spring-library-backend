package tw.ispan.librarysystem.service.borrow;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.entity.borrow.Borrow;
import tw.ispan.librarysystem.entity.borrow.Borrow.BorrowStatus;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.books.BookRepository;
import tw.ispan.librarysystem.repository.borrow.BorrowRepository;
import tw.ispan.librarysystem.service.notification.NotificationService;
import tw.ispan.librarysystem.dto.borrow.BorrowStatisticsDto;
import tw.ispan.librarysystem.dto.borrow.BorrowBatchRequestDto;
import tw.ispan.librarysystem.dto.borrow.BorrowBatchResponseDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);
    private static final int MAX_RENEW_COUNT = 2;
    private static final int BORROW_DAYS = 30;
    private static final int RENEW_DAYS = 30;
    private static final int RENEW_NOTIFICATION_DAYS = 3;
    private static final int MAX_BORROW_BOOKS = 5; // 最大借閱數量
    private static final int MAX_OVERDUE_BOOKS = 2; // 最大逾期數量

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;

    @Transactional
    public Borrow borrowBook(Integer userId, Integer bookId) {
        logger.info("處理借書請求 - 使用者ID: {}, 書籍ID: {}", userId, bookId);

        // 檢查借閱限制
        validateBorrowLimits(userId);

        // 檢查書籍是否存在且可借
        BookEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("找不到該書籍"));

        if (!book.getIsAvailable()) {
            throw new RuntimeException("該書籍目前無法借閱");
        }

        // 檢查是否已借閱該書
        if (borrowRepository.existsActiveBookBorrow(userId, bookId)) {
            throw new RuntimeException("您已經借閱了這本書");
        }

        // 建立借閱記錄
        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(BORROW_DAYS));
        borrow.setRenewCount(0);
        borrow.setStatus(BorrowStatus.BORROWED);

        // 更新書籍狀態
        book.setIsAvailable(false);
        bookRepository.save(book);

        // 儲存借閱記錄
        Borrow savedBorrow = borrowRepository.save(borrow);
        logger.info("借書成功 - 借閱ID: {}", savedBorrow.getBorrowId());
        return savedBorrow;
    }

    @Transactional
    public Borrow returnBook(Integer borrowId) {
        logger.info("處理還書請求 - 借閱ID: {}", borrowId);

        Borrow borrow = borrowRepository.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("找不到該借閱記錄"));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new RuntimeException("該書籍已經歸還");
        }

        // 更新借閱記錄
        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.RETURNED);

        // 更新書籍狀態
        BookEntity book = bookRepository.findById(borrow.getBookId())
            .orElseThrow(() -> new RuntimeException("找不到該書籍"));
        book.setIsAvailable(true);
        bookRepository.save(book);

        // 儲存借閱記錄
        Borrow savedBorrow = borrowRepository.save(borrow);
        logger.info("還書成功 - 借閱ID: {}", savedBorrow.getBorrowId());
        return savedBorrow;
    }

    @Transactional
    public Borrow renewBook(Integer borrowId) {
        logger.info("處理續借請求 - 借閱ID: {}", borrowId);

        Borrow borrow = borrowRepository.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("找不到該借閱記錄"));

        // 檢查借閱狀態
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new RuntimeException("該書籍已歸還，無法續借");
        }

        if (borrow.getStatus() == BorrowStatus.OVERDUE) {
            throw new RuntimeException("該書籍已逾期，無法續借");
        }

        // 檢查續借次數
        if (borrow.getRenewCount() >= MAX_RENEW_COUNT) {
            throw new RuntimeException("已達到最大續借次數（" + MAX_RENEW_COUNT + "次）");
        }

        // 檢查是否在可續借期間（到期前3天內）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = borrow.getDueDate();
        if (dueDate.minusDays(RENEW_NOTIFICATION_DAYS).isAfter(now)) {
            throw new RuntimeException("尚未到續借時間（到期前" + RENEW_NOTIFICATION_DAYS + "天內才能續借）");
        }

        // 更新借閱記錄
        borrow.setDueDate(dueDate.plusDays(RENEW_DAYS));
        borrow.setRenewCount(borrow.getRenewCount() + 1);
        borrow.setStatus(BorrowStatus.RENEWED);

        // 儲存借閱記錄
        Borrow savedBorrow = borrowRepository.save(borrow);

        // 發送續借成功通知
        try {
            notificationService.sendRenewalNotification(
                borrow.getUserId(),
                borrow.getBook().getTitle(),
                savedBorrow.getDueDate()
            );
        } catch (Exception e) {
            logger.warn("發送續借通知失敗", e);
        }

        logger.info("續借成功 - 借閱ID: {}", savedBorrow.getBorrowId());
        return savedBorrow;
    }

    @Transactional(readOnly = true)
    public List<Borrow> getMemberBorrowHistory(Integer userId) {
        logger.info("開始獲取會員借閱歷史 - 使用者ID: {}", userId);
        try {
            // 先檢查會員是否存在
            if (userId == null || userId <= 0) {
                logger.error("無效的使用者ID: {}", userId);
                throw new RuntimeException("無效的使用者ID");
            }

            // 使用原生 SQL 查詢來提高效能
            String sql = "SELECT b.*, bk.title, bk.author, bk.isbn, m.name, m.email " +
                        "FROM borrow_records b " +
                        "LEFT JOIN books bk ON b.book_id = bk.book_id " +
                        "LEFT JOIN members m ON b.user_id = m.user_id " +
                        "WHERE b.user_id = ? " +
                        "ORDER BY b.borrow_date DESC " +
                        "LIMIT 100";
            
            List<Borrow> borrows = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Borrow borrow = new Borrow();
                borrow.setBorrowId(rs.getInt("borrow_id"));
                borrow.setUserId(rs.getInt("user_id"));
                borrow.setBookId(rs.getInt("book_id"));
                borrow.setBorrowDate(rs.getTimestamp("borrow_date").toLocalDateTime());
                borrow.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                if (rs.getTimestamp("return_date") != null) {
                    borrow.setReturnDate(rs.getTimestamp("return_date").toLocalDateTime());
                }
                borrow.setRenewCount(rs.getInt("renew_count"));
                borrow.setStatus(Borrow.BorrowStatus.valueOf(rs.getString("status")));
                
                // 設置關聯
                BookEntity book = new BookEntity();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                borrow.setBook(book);
                
                Member member = new Member();
                member.setId(rs.getInt("user_id"));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
                borrow.setMember(member);
                
                return borrow;
            }, userId);
            
            logger.info("成功獲取 {} 筆借閱記錄", borrows.size());
            return borrows;
        } catch (Exception e) {
            logger.error("獲取借閱歷史時發生錯誤", e);
            throw new RuntimeException("獲取借閱歷史失敗: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<Borrow> getMemberBorrowHistory(Integer userId, Pageable pageable) {
        logger.info("獲取會員借閱歷史（分頁） - 使用者ID: {}", userId);
        return borrowRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Borrow> getMemberCurrentBorrows(Integer userId) {
        logger.info("獲取會員當前借閱 - 使用者ID: {}", userId);
        return borrowRepository.findByUserIdAndStatus(userId, BorrowStatus.BORROWED);
    }

    @Transactional(readOnly = true)
    public List<Borrow> getMemberOverdueBorrows(Integer userId) {
        logger.info("獲取會員逾期借閱 - 使用者ID: {}", userId);
        return borrowRepository.findByUserIdAndStatus(userId, BorrowStatus.OVERDUE);
    }

    @Transactional
    public void checkAndUpdateOverdueBorrows() {
        logger.info("檢查並更新逾期借閱");
        LocalDateTime now = LocalDateTime.now();
        List<Borrow> overdueBorrows = borrowRepository.findOverdueBorrows(now);
        
        for (Borrow borrow : overdueBorrows) {
            borrow.setStatus(BorrowStatus.OVERDUE);
            borrowRepository.save(borrow);
            logger.info("更新逾期狀態 - 借閱ID: {}", borrow.getBorrowId());
        }
    }

    @Transactional(readOnly = true)
    public boolean canRenew(Integer borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("找不到該借閱記錄"));

        return borrow.getStatus() != BorrowStatus.RETURNED &&
               borrow.getStatus() != BorrowStatus.OVERDUE &&
               borrow.getRenewCount() < MAX_RENEW_COUNT &&
               !borrow.getDueDate().minusDays(RENEW_NOTIFICATION_DAYS).isAfter(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long getBorrowCount() {
        return borrowRepository.count();
    }

    /**
     * 獲取借閱統計
     */
    @Transactional(readOnly = true)
    public BorrowStatisticsDto getBorrowStatistics(Integer userId) {
        logger.info("獲取借閱統計 - 使用者ID: {}", userId);
        
        try {
            // 使用原生SQL查詢統計資料
            String sql = """
                SELECT 
                    COUNT(*) as total_borrows,
                    SUM(CASE WHEN status = 'BORROWED' OR status = 'RENEWED' THEN 1 ELSE 0 END) as current_borrows,
                    SUM(CASE WHEN status = 'OVERDUE' THEN 1 ELSE 0 END) as overdue_borrows,
                    SUM(CASE WHEN status = 'RETURNED' THEN 1 ELSE 0 END) as returned_borrows,
                    SUM(renew_count) as total_renewals,
                    AVG(CASE WHEN return_date IS NOT NULL 
                        THEN TIMESTAMPDIFF(DAY, borrow_date, return_date) 
                        ELSE NULL END) as avg_duration
                FROM borrow_records 
                WHERE user_id = ?
                """;
            
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId);
            
            BorrowStatisticsDto statistics = new BorrowStatisticsDto();
            statistics.setTotalBorrows(((Number) result.get("total_borrows")).longValue());
            statistics.setCurrentBorrows(((Number) result.get("current_borrows")).longValue());
            statistics.setOverdueBorrows(((Number) result.get("overdue_borrows")).longValue());
            statistics.setReturnedBorrows(((Number) result.get("returned_borrows")).longValue());
            statistics.setTotalRenewals(((Number) result.get("total_renewals")).longValue());
            
            if (result.get("avg_duration") != null) {
                statistics.setAverageBorrowDuration(((Number) result.get("avg_duration")).doubleValue());
            }
            
            // 獲取最常借閱的書籍
            String mostBorrowedSql = """
                SELECT bk.book_id, bk.title, COUNT(*) as borrow_count
                FROM borrow_records b
                JOIN books bk ON b.book_id = bk.book_id
                WHERE b.user_id = ?
                GROUP BY bk.book_id, bk.title
                ORDER BY borrow_count DESC
                LIMIT 1
                """;
            
            try {
                Map<String, Object> mostBorrowed = jdbcTemplate.queryForMap(mostBorrowedSql, userId);
                statistics.setMostBorrowedBookId(((Number) mostBorrowed.get("book_id")).longValue());
                statistics.setMostBorrowedBookTitle((String) mostBorrowed.get("title"));
            } catch (Exception e) {
                logger.debug("沒有找到最常借閱的書籍", e);
            }
            
            return statistics;
        } catch (Exception e) {
            logger.error("獲取借閱統計失敗", e);
            return new BorrowStatisticsDto();
        }
    }

    /**
     * 檢查借閱限制
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkBorrowLimits(Integer userId) {
        logger.info("檢查借閱限制 - 使用者ID: {}", userId);
        
        Map<String, Object> limits = new HashMap<>();
        
        try {
            // 檢查當前借閱數量
            long currentBorrows = borrowRepository.countByUserIdAndStatus(userId, BorrowStatus.BORROWED);
            long renewedBorrows = borrowRepository.countByUserIdAndStatus(userId, BorrowStatus.RENEWED);
            long totalCurrentBorrows = currentBorrows + renewedBorrows;
            
            // 檢查逾期數量
            long overdueBorrows = borrowRepository.countByUserIdAndStatus(userId, BorrowStatus.OVERDUE);
            
            limits.put("currentBorrows", totalCurrentBorrows);
            limits.put("maxBorrows", 5); // 最大借閱數量
            limits.put("overdueBorrows", overdueBorrows);
            limits.put("maxOverdueBooks", 2); // 最大逾期數量
            limits.put("canBorrow", totalCurrentBorrows < 5);
            limits.put("hasOverdueLimit", overdueBorrows >= 2);
            
            return limits;
        } catch (Exception e) {
            logger.error("檢查借閱限制失敗", e);
            limits.put("error", "檢查借閱限制失敗");
            return limits;
        }
    }

    /**
     * 私有方法：檢查借閱限制
     */
    private void validateBorrowLimits(Integer userId) {
        Map<String, Object> limits = checkBorrowLimits(userId);
        
        if (!(Boolean) limits.get("canBorrow")) {
            throw new RuntimeException("已達到最大借閱數量限制（5本）");
        }
        
        if ((Boolean) limits.get("hasOverdueLimit")) {
            throw new RuntimeException("您有逾期書籍，請先歸還逾期書籍後再借閱");
        }
    }

    @Transactional
    public BorrowBatchResponseDto batchBorrow(Integer userId, List<BorrowBatchRequestDto.BorrowBookRequest> books) {
        logger.info("處理批量借書請求 - 使用者ID: {}, 書籍數量: {}", userId, books.size());
        
        BorrowBatchResponseDto response = new BorrowBatchResponseDto();
        response.setSuccess(true);
        response.setMessage("批量借書完成");
        response.setResults(new ArrayList<>());
        
        // 檢查借閱限制
        validateBorrowLimits(userId);
        
        // 檢查批量借書數量限制
        if (books.size() > MAX_BORROW_BOOKS) {
            throw new RuntimeException("一次最多只能借閱 " + MAX_BORROW_BOOKS + " 本書");
        }
        
        // 處理每本書的借閱
        for (BorrowBatchRequestDto.BorrowBookRequest bookRequest : books) {
            BorrowBatchResponseDto.BorrowResult result = new BorrowBatchResponseDto.BorrowResult();
            result.setBookId(bookRequest.getBookId());
            
            try {
                // 使用自定義借閱期限（如果提供）
                int borrowDays = bookRequest.getDuration() != null ? bookRequest.getDuration() : BORROW_DAYS;
                
                // 檢查書籍是否存在且可借
                BookEntity book = bookRepository.findById(bookRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException("找不到該書籍"));

                if (!book.getIsAvailable()) {
                    result.setStatus("failed");
                    result.setMessage("該書籍目前無法借閱");
                    response.getResults().add(result);
                    continue;
                }

                // 檢查是否已借閱該書
                if (borrowRepository.existsActiveBookBorrow(userId, bookRequest.getBookId())) {
                    result.setStatus("failed");
                    result.setMessage("您已經借閱了這本書");
                    response.getResults().add(result);
                    continue;
                }

                // 建立借閱記錄
                Borrow borrow = new Borrow();
                borrow.setUserId(userId);
                borrow.setBookId(bookRequest.getBookId());
                borrow.setBorrowDate(LocalDateTime.now());
                borrow.setDueDate(LocalDateTime.now().plusDays(borrowDays));
                borrow.setRenewCount(0);
                borrow.setStatus(BorrowStatus.BORROWED);

                // 更新書籍狀態
                book.setIsAvailable(false);
                bookRepository.save(book);

                // 儲存借閱記錄
                Borrow savedBorrow = borrowRepository.save(borrow);
                
                result.setStatus("success");
                result.setBorrowId(savedBorrow.getBorrowId());
                logger.info("批量借書成功 - 借閱ID: {}, 書籍ID: {}", savedBorrow.getBorrowId(), bookRequest.getBookId());
                
            } catch (Exception e) {
                result.setStatus("failed");
                result.setMessage(e.getMessage());
                logger.error("批量借書失敗 - 書籍ID: {}, 錯誤: {}", bookRequest.getBookId(), e.getMessage());
            }
            
            response.getResults().add(result);
        }
        
        return response;
    }
} 