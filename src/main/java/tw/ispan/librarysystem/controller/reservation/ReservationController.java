// @SuppressWarnings("SpellCheckingInspection")
package tw.ispan.librarysystem.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationBatchRequestDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationResponseDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationHistoryDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationConfirmRequest;
import tw.ispan.librarysystem.dto.reservation.ApiResponse;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.entity.reservation.ReservationLogEntity;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
import tw.ispan.librarysystem.service.reservation.ReservationService;
import tw.ispan.librarysystem.service.reservation.ReservationLogService;
import tw.ispan.librarysystem.service.reservation.ReservationNotificationService;
import tw.ispan.librarysystem.repository.member.MemberRepository;
import tw.ispan.librarysystem.entity.member.Member;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "書籍預約管理", description = "提供書籍預約的完整功能，包括單本預約、批量預約、取消預約、預約歷史查詢等")
@RestController
@RequestMapping("/api/bookreservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private ReservationLogService reservationLogService;

    @Autowired
    private ReservationNotificationService notificationService;

    @Autowired
    private MemberRepository memberRepository;

    // 查詢用戶預約清單
    @Operation(summary = "查詢用戶預約清單")
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(@RequestParam String userId) {
        try {
            // 如果 userId 是 'current'，使用預設用戶 ID
            Integer actualUserId;
            if ("current".equals(userId)) {
                actualUserId = 1; // 預設用戶 ID，實際應用中應該從認證系統獲取
            } else {
                actualUserId = Integer.parseInt(userId);
            }
            
            List<ReservationDTO> reservations = reservationService.getReservationsByUserId(actualUserId);
            return ResponseEntity.ok(reservations);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("預約失敗訊息：" + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // 查詢單筆預約
    @Operation(summary = "查詢單筆預約紀錄")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer reservationId) {
        return reservationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 查詢某本書的所有預約
    @Operation(summary = "查詢某本書的所有預約紀錄")
    @GetMapping("/book/{bookId}")
    public List<ReservationDTO> getReservationsByBookId(@PathVariable Integer bookId) {
        return reservationService.getReservationsByBookId(bookId);
    }

    // 單本預約
    @Operation(summary = "單本書籍預約")
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationDTO dto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
        result.setBookId(dto.getBookId());
        try {
            // 確保取書相關資訊有預設值
            if (dto.getPickupLocation() == null || dto.getPickupLocation().trim().isEmpty()) {
                dto.setPickupLocation("一樓服務台");
            }
            if (dto.getPickupMethod() == null || dto.getPickupMethod().trim().isEmpty()) {
                dto.setPickupMethod("親自取書");
            }
            
            ReservationEntity entity = reservationService.createReservation(dto);
            result.setReservationId(entity.getReservationId());
            result.setStatus("success");
            response.setSuccess(true);
        } catch (Exception e) {
            System.out.println("預約失敗訊息：" + e.getMessage());
            result.setStatus("fail");
            result.setReason(e.getMessage());
            response.setSuccess(false);
        }
        results.add(result);
        response.setResults(results);
        return ResponseEntity.ok(response);
    }

    // 批量預約
    @Operation(summary = "批量預約多本書籍")
    @PostMapping("/batch")
    public ResponseEntity<ReservationResponseDTO> batchReservation(@RequestBody ReservationBatchRequestDTO batchDto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        
        // 生成統一的批次預約編號
        String batchReservationId = "BATCH_" + System.currentTimeMillis();
        
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        List<ReservationEntity> successfulReservations = new ArrayList<>();
        boolean allSuccess = true;
        
        for (ReservationBatchRequestDTO.BookReserveItem item : batchDto.getBooks()) {
            ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
            result.setBookId(item.getBookId());
            try {
                ReservationDTO dto = new ReservationDTO();
                dto.setBookId(item.getBookId());
                dto.setUserId(batchDto.getUserId());
                dto.setStatus(ReservationEntity.STATUS_PENDING);
                if (item.getReserveTime() == null) {
                    throw new RuntimeException("資料缺失");
                }
                dto.setReserveTime(java.time.LocalDateTime.parse(item.getReserveTime()));
                dto.setBatchId(batchReservationId);
                
                // 設定取書相關資訊
                dto.setPickupLocation(batchDto.getPickupLocation() != null ? batchDto.getPickupLocation() : "一樓服務台");
                dto.setPickupMethod(batchDto.getPickupMethod() != null ? batchDto.getPickupMethod() : "親自取書");
                
                ReservationEntity entity = reservationService.createReservation(dto);
                result.setReservationId(entity.getReservationId());
                result.setStatus("success");
                
                // 收集成功的預約
                successfulReservations.add(entity);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("預約失敗訊息：時間格式錯誤");
                result.setStatus("fail");
                result.setReason("資料缺失");
                allSuccess = false;
            } catch (NullPointerException e) {
                System.out.println("預約失敗訊息：欄位為空");
                result.setStatus("fail");
                result.setReason("資料缺失");
                allSuccess = false;
            } catch (Exception e) {
                System.out.println("預約失敗訊息：" + e.getMessage());
                result.setStatus("fail");
                result.setReason(e.getMessage());
                allSuccess = false;
                e.printStackTrace();
            }
            results.add(result);
        }
        
        response.setSuccess(allSuccess);
        response.setResults(results);
        response.setBatchReservationId(batchReservationId); // 回傳統一編號
        
        // 如果有成功的預約，發送批量通知郵件
        if (!successfulReservations.isEmpty()) {
            try {
                // 根據 userId 查找會員資訊
                Member member = memberRepository.findById(batchDto.getUserId().longValue()).orElse(null);
                if (member != null) {
                    notificationService.sendBatchReservationSuccessEmail(member, successfulReservations, batchReservationId);
                }
            } catch (Exception e) {
                // 郵件發送失敗不影響預約流程，只記錄錯誤
                System.err.println("發送批量預約成功通知郵件失敗：" + e.getMessage());
            }
        }
        
        try {
            System.out.println("批量預約回傳內容：" + new ObjectMapper().writeValueAsString(response));
        } catch (Exception e) {
            System.out.println("回傳內容序列化失敗：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // 取消預約
    @Operation(summary = "取消單筆預約")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    // 將狀態改為 CANCELLED 而不是刪除
                    reservation.setStatus(ReservationEntity.STATUS_CANCELLED);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    ReservationEntity updated = reservationRepository.save(reservation);
                    
                    // 回傳更新後的預約資訊
                    return ResponseEntity.ok(reservationService.convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 取消預約 API
    @Operation(summary = "將預約狀態設為取消")
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Integer reservationId) {
        try {
            return reservationRepository.findById(reservationId)
                    .map(reservation -> {
                        // 檢查預約狀態
                        if (ReservationEntity.STATUS_CANCELLED.equals(reservation.getStatus())) {
                            return ResponseEntity.badRequest().body("此預約已經被取消");
                        }
                        if (ReservationEntity.STATUS_COMPLETED.equals(reservation.getStatus())) {
                            return ResponseEntity.badRequest().body("此預約已經完成，無法取消");
                        }
                        
                        // 將狀態改為 CANCELLED
                        reservation.setStatus(ReservationEntity.STATUS_CANCELLED);
                        reservation.setUpdatedAt(LocalDateTime.now());
                        ReservationEntity updated = reservationRepository.save(reservation);
                        
                        // 回傳成功訊息
                        return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "預約取消成功",
                            "reservation", reservationService.convertToDTO(updated)
                        ));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "取消預約失敗：" + e.getMessage()
            ));
        }
    }

    // 狀態轉換
    @Operation(summary = "更新預約狀態")
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Integer reservationId, @RequestBody ReservationDTO dto) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservation.setStatus(dto.getStatus());
                    reservation.setUpdatedAt(LocalDateTime.now());
                    ReservationEntity updated = reservationRepository.save(reservation);
                    return ResponseEntity.ok(reservationService.convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 批量刪除預約
    @Operation(summary = "批量刪除預約紀錄")
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteReservations(@RequestBody BatchDeleteRequest request) {
        try {
            reservationRepository.deleteAllById(request.getReservationIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量刪除失敗：" + e.getMessage());
        }
    }

    // 批量取消預約
    @Operation(summary = "批量取消預約紀錄")
    @PutMapping("/batch/cancel")
    public ResponseEntity<?> batchCancelReservations(@RequestBody BatchDeleteRequest request) {
        try {
            List<ReservationEntity> reservations = reservationRepository.findAllById(request.getReservationIds());
            int cancelledCount = 0;
            
            for (ReservationEntity reservation : reservations) {
                if (!ReservationEntity.STATUS_CANCELLED.equals(reservation.getStatus()) && !ReservationEntity.STATUS_COMPLETED.equals(reservation.getStatus())) {
                    reservation.setStatus(ReservationEntity.STATUS_CANCELLED);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    reservationRepository.save(reservation);
                    cancelledCount++;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("成功取消 %d 筆預約", cancelledCount),
                "cancelledCount", cancelledCount,
                "totalRequested", request.getReservationIds().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "批量取消失敗：" + e.getMessage()
            ));
        }
    }

    // 新的批量取消預約 API 端點
    @Operation(summary = "新的批量取消預約 API")
    @PutMapping("/batch-cancel")
    public ResponseEntity<?> batchCancelReservationsNew(@RequestBody BatchCancelRequest request) {
        try {
            Map<String, Object> result = reservationService.batchCancelReservations(request.getReservationIds());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "批量取消失敗：" + e.getMessage()
            ));
        }
    }

    // 批量刪除請求 DTO
    public static class BatchDeleteRequest {
        private List<Integer> reservationIds;
        public List<Integer> getReservationIds() { return reservationIds; }
        public void setReservationIds(List<Integer> reservationIds) { this.reservationIds = reservationIds; }
    }

    // 批量取消請求 DTO
    public static class BatchCancelRequest {
        private List<Integer> reservationIds;
        public List<Integer> getReservationIds() { return reservationIds; }
        public void setReservationIds(List<Integer> reservationIds) { this.reservationIds = reservationIds; }
    }

    // 新增：預約歷史查詢 API
    @Operation(summary = "查詢預約歷史紀錄")
    @GetMapping("/history")
    public ResponseEntity<List<ReservationHistoryDTO>> getReservationHistory(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false, defaultValue = "true") boolean includeCancelled
    ) {
        try {
            if (userId != null) {
                // 查詢特定會員的預約歷史
                List<ReservationHistoryDTO> history = reservationService.getReservationHistoryByUserId(userId, includeCancelled);
                return ResponseEntity.ok(history);
            } else {
                // 查詢所有預約歷史 (管理員功能)
                List<ReservationHistoryDTO> history = reservationService.getAllReservationHistory(includeCancelled);
                return ResponseEntity.ok(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "確認預約")
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse> confirmReservation(@RequestBody ReservationConfirmRequest request) {
        try {
            // 1. 檢查預約日誌是否存在
            Optional<ReservationLogEntity> logOpt = reservationLogService.getLogById(request.getLogId());
            if (!logOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "找不到預約日誌記錄"));
            }
            
            ReservationLogEntity log = logOpt.get();
            
            // 2. 檢查使用者身份
            if (!log.getUserId().equals(request.getUserId())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "無權限確認此預約"));
            }
            
            // 3. 檢查書籍是否一致
            if (!log.getBook().getBookId().equals(request.getBookId())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "書籍資訊不符"));
            }
            
            // 4. 檢查狀態是否為 PENDING
            if (!"PENDING".equals(log.getStatus())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "此預約已被處理"));
            }
            
            // 5. 建立正式預約記錄
            ReservationEntity reservation = reservationService.createReservation(log);
            
            // 6. 更新預約日誌狀態
            reservationLogService.updateLogStatus(log, "CONFIRMED");
            
            // 7. 建立回應
            ApiResponse response = new ApiResponse(true, "預約確認成功");
            response.setReservationId(reservation.getReservationId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
} 