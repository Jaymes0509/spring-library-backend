package tw.ispan.librarysystem.controller.books;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.ispan.librarysystem.dto.BookDTO;
import tw.ispan.librarysystem.dto.PageResponseDTO;
import tw.ispan.librarysystem.dto.SearchCondition;
import tw.ispan.librarysystem.dto.BookSimpleDTO;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.mapper.BookMapper;
import tw.ispan.librarysystem.repository.books.BookRepository;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
import tw.ispan.librarysystem.service.books.BookDetailService;
import tw.ispan.librarysystem.service.books.BookService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookDetailService bookDetailService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @PostMapping("/fill-details")
    public ResponseEntity<String> fillMissingBookDetails() {
        bookDetailService.updateMissingCoversAndSummaries();
        return ResponseEntity.ok("補齊完成！");
    }
    
    @GetMapping("/{bookId}")
    public BookDTO getBookById(@PathVariable Integer bookId) {
        BookEntity book = bookService.findById(bookId).orElse(null);
        return bookMapper.toDTO(book);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        Optional<BookEntity> optional = bookService.findByIsbn(isbn);
    if (optional.isPresent()) {
        BookDTO dto = bookMapper.toDTO(optional.get());
        return ResponseEntity.ok(dto);
    } else {
        return ResponseEntity.notFound().build();
    }
}

    @PostMapping("/{isbn}/reserve")
    public ResponseEntity<?> reserveBook(@PathVariable String isbn, @RequestBody(required = false) ReservationRequest request) {
        try {
            System.out.println("開始處理預約請求，ISBN: " + isbn);
            
            // 根據 ISBN 查找書籍
            Optional<BookEntity> bookOptional = bookService.findByIsbn(isbn);
            if (!bookOptional.isPresent()) {
                System.out.println("找不到 ISBN 為 " + isbn + " 的書籍");
                return ResponseEntity.notFound().build();
            }
            
            BookEntity book = bookOptional.get();
            System.out.println("找到書籍: " + book.getTitle() + " (ID: " + book.getBookId() + ")");
            
            // 創建預約記錄
            ReservationEntity reservation = new ReservationEntity();
            reservation.setBook(book);
            reservation.setUserId(request != null ? request.getUserId() : 1); // 預設用戶ID為1
            reservation.setReserveTime(LocalDateTime.now());
            reservation.setExpiryDate(LocalDateTime.now().plusDays(3)); // 3天後過期
            reservation.setStatus("PENDING");
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setUpdatedAt(LocalDateTime.now());
            
            System.out.println("準備儲存預約記錄...");
            System.out.println("預約記錄內容: " + reservation.toString());
            
            ReservationEntity savedReservation = reservationRepository.save(reservation);
            System.out.println("預約記錄儲存成功，ID: " + savedReservation.getReservationId());
            
            return ResponseEntity.ok("預約成功！預約編號：" + savedReservation.getReservationId());
            
        } catch (Exception e) {
            System.err.println("預約失敗，錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("預約失敗：" + e.getMessage());
        }
    }

    @GetMapping("/simple-search")
    public PageResponseDTO<BookSimpleDTO> simpleSearch(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "title") String sortField,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<BookSimpleDTO> bookPage = bookService.simpleSearch(null,keyword, pageable);
        return new PageResponseDTO<>(bookPage.getContent(), bookPage.getNumber(), bookPage.getSize(), bookPage.getTotalElements(), bookPage.getTotalPages(), bookPage.isLast(), bookPage.isFirst());
    }

    @PostMapping("/advanced-search")
    public PageResponseDTO<BookDTO> advancedSearch(
        @RequestBody List<SearchCondition> conditions,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "title") String sortField,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<BookDTO> bookPage = bookService.advancedSearch(conditions, pageable);
        return new PageResponseDTO<>(bookPage.getContent(), bookPage.getNumber(), bookPage.getSize(), bookPage.getTotalElements(), bookPage.getTotalPages(), bookPage.isLast(), bookPage.isFirst());
    }

    @GetMapping("/check-reservations-table")
    public ResponseEntity<?> checkReservationsTable() {
        try {
            // 嘗試查詢預約表
            List<ReservationEntity> reservations = reservationRepository.findAll();
            return ResponseEntity.ok("reservations 表存在，當前有 " + reservations.size() + " 筆記錄");
        } catch (Exception e) {
            System.err.println("檢查 reservations 表失敗: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("reservations 表檢查失敗: " + e.getMessage());
        }
    }

    @GetMapping("/test-db")
    public ResponseEntity<?> testDatabase() {
        try {
            // 測試書籍查詢
            List<BookEntity> books = bookService.findAll(PageRequest.of(0, 5)).getContent();
            System.out.println("找到 " + books.size() + " 本書籍");
            
            // 測試預約查詢
            List<ReservationEntity> reservations = reservationRepository.findAll();
            System.out.println("找到 " + reservations.size() + " 筆預約記錄");
            
            return ResponseEntity.ok("資料庫連接正常，書籍數量: " + books.size() + ", 預約數量: " + reservations.size());
        } catch (Exception e) {
            System.err.println("資料庫測試失敗: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("資料庫測試失敗: " + e.getMessage());
        }
    }

    // 內部類別用於接收預約請求
    public static class ReservationRequest {
        private Integer userId;
        
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
}