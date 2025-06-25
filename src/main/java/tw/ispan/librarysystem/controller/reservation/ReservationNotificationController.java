package tw.ispan.librarysystem.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.service.reservation.ReservationNotificationService;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.repository.member.MemberRepository;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
import tw.ispan.librarysystem.repository.books.BookRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Tag(name = "預約通知測試", description = "測試預約成功通知郵件發送功能")
@RestController
@RequestMapping("/api/reservation-notification")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationNotificationController {

    @Autowired
    private ReservationNotificationService notificationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Operation(summary = "測試極簡郵件發送功能")
    @PostMapping("/test-minimal-email")
    public ResponseEntity<Map<String, Object>> testMinimalEmail(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email").toString();
            
            // 創建一個極簡的測試郵件
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setTo(email);
            testMessage.setSubject("Test");
            testMessage.setText("Test email content.");
            testMessage.setFrom("ispanlibrarysystem@gmail.com");
            
            // 發送測試郵件
            mailSender.send(testMessage);
            
            response.put("success", true);
            response.put("message", "極簡測試郵件已發送");
            response.put("email", email);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送極簡測試郵件失敗：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "測試基本郵件發送功能")
    @PostMapping("/test-simple-email")
    public ResponseEntity<Map<String, Object>> testSimpleEmail(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email").toString();
            
            // 創建一個簡單的測試郵件
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setTo(email);
            testMessage.setSubject("Test Email");
            testMessage.setText("This is a test email from the library system.\n\nIf you receive this email, the mail service is working correctly.");
            testMessage.setFrom("ispanlibrarysystem@gmail.com");
            
            // 發送測試郵件
            mailSender.send(testMessage);
            
            response.put("success", true);
            response.put("message", "測試郵件已發送");
            response.put("email", email);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送測試郵件失敗：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "發送預約通知郵件")
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotificationEmail(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = Integer.valueOf(request.get("userId").toString());
            
            // 支援單個 reservationId 或多個 reservationIds
            Object reservationIdObj = request.get("reservationId");
            Object reservationIdsObj = request.get("reservationIds");
            
            if (reservationIdObj != null) {
                // 單個預約ID
                Integer reservationId = Integer.valueOf(reservationIdObj.toString());
                ReservationEntity reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
                
                notificationService.sendReservationSuccessEmail(reservation);
                
                response.put("success", true);
                response.put("message", "預約通知郵件已發送");
                response.put("reservationId", reservationId);
                response.put("userId", userId);
                
            } else if (reservationIdsObj != null) {
                // 多個預約ID（數組）
                @SuppressWarnings("unchecked")
                java.util.List<Object> reservationIds = (java.util.List<Object>) reservationIdsObj;
                
                java.util.List<ReservationEntity> reservations = new java.util.ArrayList<>();
                for (Object idObj : reservationIds) {
                    Integer reservationId = Integer.valueOf(idObj.toString());
                    ReservationEntity reservation = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new RuntimeException("找不到預約記錄 ID: " + reservationId));
                    reservations.add(reservation);
                }
                
                // 查找會員資訊
                Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到會員資訊"));
                
                // 生成批次ID
                String batchId = "BATCH_" + System.currentTimeMillis();
                
                // 發送批量通知郵件
                notificationService.sendBatchReservationSuccessEmail(member, reservations, batchId);
                
                response.put("success", true);
                response.put("message", "批量預約通知郵件已發送");
                response.put("reservationIds", reservationIds);
                response.put("userId", userId);
                response.put("batchId", batchId);
                response.put("reservationCount", reservations.size());
                
            } else {
                throw new RuntimeException("缺少 reservationId 或 reservationIds 參數");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送郵件失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "測試發送預約成功通知郵件")
    @PostMapping("/test-email")
    public ResponseEntity<Map<String, Object>> testReservationEmail(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = Integer.valueOf(request.get("userId").toString());
            Integer reservationId = Integer.valueOf(request.get("reservationId").toString());
            
            // 查找預約記錄
            ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
            
            // 發送通知郵件
            notificationService.sendReservationSuccessEmail(reservation);
            
            response.put("success", true);
            response.put("message", "預約成功通知郵件已發送");
            response.put("reservationId", reservationId);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送郵件失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "測試發送批量預約成功通知郵件")
    @PostMapping("/test-batch-email")
    public ResponseEntity<Map<String, Object>> testBatchReservationEmail(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = Integer.valueOf(request.get("userId").toString());
            String batchId = request.get("batchId").toString();
            
            // 查找該批次的所有預約記錄
            java.util.List<ReservationEntity> reservations = reservationRepository.findAll().stream()
                .filter(r -> batchId.equals(r.getBatchId()))
                .collect(java.util.stream.Collectors.toList());
            
            if (reservations.isEmpty()) {
                response.put("success", false);
                response.put("message", "找不到該批次的預約記錄");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 查找會員資訊
            Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到會員資訊"));
            
            // 發送批量通知郵件
            notificationService.sendBatchReservationSuccessEmail(member, reservations, batchId);
            
            response.put("success", true);
            response.put("message", "批量預約成功通知郵件已發送");
            response.put("batchId", batchId);
            response.put("userId", userId);
            response.put("reservationCount", reservations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送郵件失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "測試創建模擬預約並發送通知郵件")
    @PostMapping("/test-create-and-notify")
    public ResponseEntity<Map<String, Object>> testCreateReservationAndNotify(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = Integer.valueOf(request.get("userId").toString());
            Integer bookId = Integer.valueOf(request.get("bookId").toString());
            
            // 查找會員和書籍資訊
            Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到會員資訊"));
            
            BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("找不到書籍資訊"));
            
            // 創建模擬預約記錄
            ReservationEntity mockReservation = new ReservationEntity();
            mockReservation.setUserId(userId);
            mockReservation.setBook(book);
            mockReservation.setReserveTime(LocalDateTime.now());
            mockReservation.setExpiryDate(LocalDateTime.now().plusDays(3));
            mockReservation.setStatus(ReservationEntity.STATUS_PENDING);
            mockReservation.setReserveStatus(1);
            mockReservation.setCreatedAt(LocalDateTime.now());
            mockReservation.setUpdatedAt(LocalDateTime.now());
            mockReservation.setPickupLocation("First Floor Service Desk");
            mockReservation.setPickupMethod("Self Pickup");
            
            // 發送通知郵件
            notificationService.sendReservationSuccessEmail(member, List.of(mockReservation), null);
            
            response.put("success", true);
            response.put("message", "模擬預約通知郵件已發送");
            response.put("userId", userId);
            response.put("bookId", bookId);
            response.put("bookTitle", book.getTitle());
            response.put("memberName", member.getName());
            response.put("memberEmail", member.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送郵件失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 