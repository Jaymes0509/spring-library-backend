// 預約服務層，處理預約相關的商業邏輯
// @SuppressWarnings("SpellCheckingInspection")
package tw.ispan.librarysystem.service.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationHistoryDTO;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.entity.reservation.ReservationLogEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
import tw.ispan.librarysystem.repository.borrow.BorrowRepository;
import tw.ispan.librarysystem.repository.books.BookRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReservationNotificationService notificationService;

    public List<ReservationDTO> getAllReservationsWithBookInfo() {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReservationDTO> getReservationById(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::convertToDTO);
    }

    public List<ReservationDTO> getReservationsByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getReservationsByBookId(Integer bookId) {
        return reservationRepository.findByBookBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO convertToDTO(ReservationEntity reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setReserveTime(reservation.getReserveTime());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        dto.setBatchId(reservation.getBatchId());

        // 設定取書相關資訊
        dto.setPickupLocation(reservation.getPickupLocation());
        dto.setPickupMethod(reservation.getPickupMethod());
        dto.setPickupTime(reservation.getReserveTime().plusDays(7)); // 預約後7天內取書

        if (reservation.getBook() != null) {
            dto.setBookId(reservation.getBook().getBookId());
            dto.setTitle(reservation.getBook().getTitle());
            dto.setIsbn(reservation.getBook().getIsbn());
            dto.setAuthor(reservation.getBook().getAuthor());
            dto.setPublisher(reservation.getBook().getPublisher());
            dto.setClassification(reservation.getBook().getClassification());
            
            if (reservation.getBook().getCategory() != null) {
                dto.setCategoryName(reservation.getBook().getCategory().getcName());
            }
        }

        return dto;
    }

    // 新增：建立預約（單本）
    public ReservationEntity createReservation(ReservationDTO dto) {
        return createReservation(dto, true); // 預設發送郵件
    }

    public ReservationEntity createReservation(ReservationDTO dto, boolean sendEmail) {
        // 1. 檢查 bookId
        if (dto.getBookId() == null) {
            throw new RuntimeException("資料缺失");
        }
        
        // 2. 檢查用戶的待領取和已領取書籍數量是否已達上限（10本）
        long activeReservationsCount = reservationRepository.findByUserId(dto.getUserId()).stream()
            .filter(r -> ReservationEntity.STATUS_PENDING.equals(r.getStatus()) || 
                        ReservationEntity.STATUS_COMPLETED.equals(r.getStatus()))
            .count();
        
        if (activeReservationsCount >= 10) {
            throw new RuntimeException("您已有 " + activeReservationsCount + " 本待領取或已領取的書籍，已達上限（10本），無法再進行預約");
        }
        
        // 3. 檢查是否已預約過（PENDING 狀態）
        boolean alreadyReserved = reservationRepository
            .findByUserId(dto.getUserId()).stream()
            .anyMatch(r -> r.getBook() != null && r.getBook().getBookId().equals(dto.getBookId()) && ReservationEntity.STATUS_PENDING.equals(r.getStatus()));
        if (alreadyReserved) {
            throw new RuntimeException("已預約過");
        }
        
        // 4. 檢查書籍是否已借出
        boolean isBorrowed = borrowRepository.existsActiveBookBorrow(dto.getUserId().longValue(), dto.getBookId());
        if (isBorrowed) {
            throw new RuntimeException("書籍已借出");
        }
        
        ReservationEntity entity = new ReservationEntity();
        entity.setUserId(dto.getUserId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : ReservationEntity.STATUS_PENDING);
        entity.setReserveStatus(1); // 設置預約狀態為成功
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setReserveTime(dto.getReserveTime());
        entity.setExpiryDate(dto.getReserveTime() != null ? dto.getReserveTime().plusDays(7) : null);
        
        // 設定 batchId
        if (dto.getBatchId() != null) {
            entity.setBatchId(dto.getBatchId());
        }
        
        // 設定取書相關資訊
        entity.setPickupLocation(dto.getPickupLocation());
        entity.setPickupMethod(dto.getPickupMethod());
        
        // 關聯書籍 - 從資料庫查詢完整的書籍資訊
        BookEntity book = bookRepository.findById(dto.getBookId())
            .orElseThrow(() -> new RuntimeException("找不到書籍資訊，bookId: " + dto.getBookId()));
        entity.setBook(book);
        
        // 儲存預約
        ReservationEntity savedEntity = reservationRepository.save(entity);
        
        // 只有在需要時才發送預約成功通知郵件
        if (sendEmail) {
            try {
                notificationService.sendReservationSuccessEmail(savedEntity);
            } catch (Exception e) {
                // 郵件發送失敗不影響預約流程，只記錄錯誤
                System.err.println("發送預約成功通知郵件失敗：" + e.getMessage());
            }
        }
        
        return savedEntity;
    }

    // 從預約日誌建立正式預約
    public ReservationEntity createReservation(ReservationLogEntity log) {
        return createReservation(log, true); // 預設發送郵件
    }

    // 從預約日誌建立正式預約
    public ReservationEntity createReservation(ReservationLogEntity log, boolean sendEmail) {
        // 檢查用戶的待領取和已領取書籍數量是否已達上限（10本）
        long activeReservationsCount = reservationRepository.findByUserId(log.getUserId().intValue()).stream()
            .filter(r -> ReservationEntity.STATUS_PENDING.equals(r.getStatus()) || 
                        ReservationEntity.STATUS_COMPLETED.equals(r.getStatus()))
            .count();
        
        if (activeReservationsCount >= 10) {
            throw new RuntimeException("您已有 " + activeReservationsCount + " 本待領取或已領取的書籍，已達上限（10本），無法再進行預約");
        }
        
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(log.getUserId().intValue()); // 轉換 Long 到 Integer
        reservation.setBook(log.getBook());
        reservation.setReserveTime(log.getReserveTime());
        reservation.setStatus(ReservationEntity.STATUS_PENDING);
        reservation.setReserveStatus(1); // 設置預約狀態為成功
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        reservation.setExpiryDate(log.getReserveTime().plusDays(7));
        
        // 設定預設的取書相關資訊
        reservation.setPickupLocation("一樓服務台");
        reservation.setPickupMethod("親自取書");
        
        // 儲存預約
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        
        // 只有在需要時才發送預約成功通知郵件
        if (sendEmail) {
            try {
                notificationService.sendReservationSuccessEmail(savedReservation);
            } catch (Exception e) {
                // 郵件發送失敗不影響預約流程，只記錄錯誤
                System.err.println("發送預約成功通知郵件失敗：" + e.getMessage());
            }
        }
        
        return savedReservation;
    }

    // 新增：查詢用戶預約統計資訊
    public Map<String, Object> getUserReservationStats(Integer userId) {
        List<ReservationEntity> userReservations = reservationRepository.findByUserId(userId);
        
        long pendingCount = userReservations.stream()
            .filter(r -> ReservationEntity.STATUS_PENDING.equals(r.getStatus()))
            .count();
            
        long completedCount = userReservations.stream()
            .filter(r -> ReservationEntity.STATUS_COMPLETED.equals(r.getStatus()))
            .count();
            
        long cancelledCount = userReservations.stream()
            .filter(r -> ReservationEntity.STATUS_CANCELLED.equals(r.getStatus()))
            .count();
            
        long totalActiveCount = pendingCount + completedCount;
        boolean canReserve = totalActiveCount < 10;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", pendingCount);
        stats.put("completedCount", completedCount);
        stats.put("cancelledCount", cancelledCount);
        stats.put("totalActiveCount", totalActiveCount);
        stats.put("maxAllowed", 10);
        stats.put("canReserve", canReserve);
        stats.put("remainingSlots", Math.max(0, 10 - totalActiveCount));
        
        return stats;
    }

    // 新增：查詢特定用戶的預約歷史
    public List<ReservationHistoryDTO> getReservationHistoryByUserId(String userId) {
        return getReservationHistoryByUserId(userId, true);
    }
    
    public List<ReservationHistoryDTO> getReservationHistoryByUserId(String userId, boolean includeCancelled) {
        try {
            Integer userIdInt = Integer.parseInt(userId);
            List<ReservationEntity> reservations = reservationRepository.findByUserId(userIdInt);
            
            if (!includeCancelled) {
                // 過濾掉已取消的預約
                reservations = reservations.stream()
                    .filter(r -> !ReservationEntity.STATUS_CANCELLED.equals(r.getStatus()))
                    .collect(Collectors.toList());
            }
            
            return reservations.stream()
                    .map(this::convertToHistoryDTO)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid userId format: " + userId);
        }
    }

    // 新增：查詢所有預約歷史 (管理員功能)
    public List<ReservationHistoryDTO> getAllReservationHistory() {
        return getAllReservationHistory(true);
    }
    
    public List<ReservationHistoryDTO> getAllReservationHistory(boolean includeCancelled) {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        
        if (!includeCancelled) {
            // 過濾掉已取消的預約
            reservations = reservations.stream()
                .filter(r -> !ReservationEntity.STATUS_CANCELLED.equals(r.getStatus()))
                .collect(Collectors.toList());
        }
        
        return reservations.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    // 新增：轉換為 ReservationHistoryDTO
    private ReservationHistoryDTO convertToHistoryDTO(ReservationEntity reservation) {
        ReservationHistoryDTO dto = new ReservationHistoryDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setReserveTime(reservation.getReserveTime());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        dto.setBatchId(reservation.getBatchId());
        dto.setPickupLocation(reservation.getPickupLocation());
        dto.setPickupMethod(reservation.getPickupMethod());

        if (reservation.getBook() != null) {
            dto.setBookId(reservation.getBook().getBookId());
            dto.setTitle(reservation.getBook().getTitle());
            dto.setIsbn(reservation.getBook().getIsbn());
            dto.setAuthor(reservation.getBook().getAuthor());
            dto.setPublisher(reservation.getBook().getPublisher());
            
            // 添加圖片網址
            if (reservation.getBook().getBookDetail() != null) {
                dto.setImgUrl(reservation.getBook().getBookDetail().getImgUrl());
            }
            
            if (reservation.getBook().getCategory() != null) {
                dto.setCategoryName(reservation.getBook().getCategory().getcName());
            }
        }

        return dto;
    }

    // 批量取消預約
    public Map<String, Object> batchCancelReservations(List<Integer> reservationIds) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        for (Integer reservationId : reservationIds) {
            Map<String, Object> result = new HashMap<>();
            result.put("reservationId", reservationId);
            
            try {
                ReservationEntity reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
                
                // 檢查預約狀態
                if (ReservationEntity.STATUS_CANCELLED.equals(reservation.getStatus())) {
                    result.put("success", false);
                    result.put("message", "此預約已經被取消");
                    failCount++;
                } else if (ReservationEntity.STATUS_COMPLETED.equals(reservation.getStatus())) {
                    result.put("success", false);
                    result.put("message", "此預約已經完成，無法取消");
                    failCount++;
                } else {
                    // 執行取消邏輯
                    reservation.setStatus(ReservationEntity.STATUS_CANCELLED);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    reservationRepository.save(reservation);
                    
                    // 更新書籍庫存狀態
                    if (reservation.getBook() != null) {
                        updateBookAvailability(reservation.getBook().getBookId());
                    }
                    
                    result.put("success", true);
                    result.put("message", "取消成功");
                    successCount++;
                }
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "取消失敗：" + e.getMessage());
                failCount++;
            }
            
            results.add(result);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", String.format("批量取消完成，成功：%d 筆，失敗：%d 筆", successCount, failCount));
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("totalRequested", reservationIds.size());
        response.put("results", results);
        
        return response;
    }
    
    // 更新書籍可用性
    private void updateBookAvailability(Integer bookId) {
        try {
            // 檢查是否還有其他有效的預約
            long activeReservations = reservationRepository.findByBookBookId(bookId)
                .stream()
                .filter(r -> ReservationEntity.STATUS_PENDING.equals(r.getStatus()))
                .count();
            
            // 如果沒有其他有效預約，將書籍設為可用
            if (activeReservations == 0) {
                BookEntity book = bookRepository.findById(bookId).orElse(null);
                if (book != null) {
                    book.setIsAvailable(true);
                    bookRepository.save(book);
                }
            }
        } catch (Exception e) {
            // 書籍狀態更新失敗不影響預約取消
            System.err.println("更新書籍狀態失敗：" + e.getMessage());
        }
    }

    // 新增：取消預約（將狀態設為 CANCELLED）
    public void cancelReservation(Integer reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
        
        // 檢查預約狀態
        if (ReservationEntity.STATUS_CANCELLED.equals(reservation.getStatus())) {
            throw new RuntimeException("此預約已經被取消");
        }
        if (ReservationEntity.STATUS_COMPLETED.equals(reservation.getStatus())) {
            throw new RuntimeException("此預約已經完成，無法取消");
        }
        
        // 將狀態改為 CANCELLED
        reservation.setStatus(ReservationEntity.STATUS_CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        // 更新書籍可用性
        if (reservation.getBook() != null) {
            updateBookAvailability(reservation.getBook().getBookId());
        }
    }

    // 新增：刪除預約（實際上是取消預約的別名）
    public void deleteReservation(Integer reservationId) {
        cancelReservation(reservationId);
    }
} 