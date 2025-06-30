//
//// @SuppressWarnings("SpellCheckingInspection")
//package tw.ispan.librarysystem.controller.reservation;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
//import tw.ispan.librarysystem.dto.reservation.ReservationBatchRequestDTO;
//import tw.ispan.librarysystem.dto.reservation.ReservationResponseDTO;
//import tw.ispan.librarysystem.dto.reservation.ReservationHistoryDTO;
//import tw.ispan.librarysystem.dto.reservation.ReservationConfirmRequest;
//import tw.ispan.librarysystem.dto.reservation.ApiResponse;
//import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
//import tw.ispan.librarysystem.entity.reservation.ReservationLogEntity;
//import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
//import tw.ispan.librarysystem.service.reservation.ReservationService;
//import tw.ispan.librarysystem.service.reservation.ReservationLogService;
//import tw.ispan.librarysystem.service.reservation.ReservationNotificationService;
//import tw.ispan.librarysystem.repository.member.MemberRepository;
//import tw.ispan.librarysystem.entity.member.Member;
//import tw.ispan.librarysystem.service.member.MemberService;
//import tw.ispan.librarysystem.security.CheckJwt;
//import tw.ispan.librarysystem.util.JwtTool;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Map;
//import java.util.HashMap;
//
//@Tag(name = "書籍預約管理", description = "提供書籍預約的完整功能，包括單本預約、批量預約、取消預約、預約歷史查詢等")
//@RestController
//@RequestMapping("/api/bookreservations")
//@CrossOrigin(origins = "http://localhost:3000")
//public class ReservationController {
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private ReservationService reservationService;
//
//    @Autowired
//    private ReservationLogService reservationLogService;
//
//    @Autowired
//    private ReservationNotificationService notificationService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MemberService memberService;
//
//    // 從 JWT token 獲取用戶 ID 的輔助方法
//    private Integer getUserIdFromToken(String authHeader) {
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            Long userId = JwtTool.parseUserIdFromToken(token);
//            return userId.intValue();
//        } catch (Exception e) {
//            throw new RuntimeException("無法從 token 獲取用戶資訊：" + e.getMessage());
//        }
//    }
//
//    // 查詢用戶預約清單
//    @Operation(summary = "查詢用戶預約清單")
//    @GetMapping
//    @CheckJwt
//    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(@RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//            List<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId);
//            return ResponseEntity.ok(reservations);
//        } catch (Exception e) {
//            System.out.println("查詢預約失敗：" + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // 查詢單筆預約
//    @Operation(summary = "查詢單筆預約紀錄")
//    @GetMapping("/{reservationId}")
//    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer reservationId) {
//        return reservationService.getReservationById(reservationId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // 查詢某本書的所有預約
//    @Operation(summary = "查詢某本書的所有預約紀錄")
//    @GetMapping("/book/{bookId}")
//    public List<ReservationDTO> getReservationsByBookId(@PathVariable Integer bookId) {
//        return reservationService.getReservationsByBookId(bookId);
//    }
//
//    // 單本預約
//    @Operation(summary = "單本書籍預約")
//    @PostMapping
//    @CheckJwt
//    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationDTO dto, @RequestHeader("Authorization") String authHeader) {
//        ReservationResponseDTO response = new ReservationResponseDTO();
//        List<ReservationResponseDTO.Result> results = new ArrayList<>();
//        ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
//        result.setBookId(dto.getBookId());
//        try {
//            // 從 JWT token 獲取真實的用戶 ID
//            Integer userId = getUserIdFromToken(authHeader);
//            dto.setUserId(userId);
//
//            // 確保取書相關資訊有預設值
//            if (dto.getPickupLocation() == null || dto.getPickupLocation().trim().isEmpty()) {
//                dto.setPickupLocation("一樓服務台");
//            }
//            if (dto.getPickupMethod() == null || dto.getPickupMethod().trim().isEmpty()) {
//                dto.setPickupMethod("親自取書");
//            }
//
//            ReservationEntity entity = reservationService.createReservation(dto, true);
//            result.setReservationId(entity.getReservationId());
//            result.setStatus("success");
//            response.setSuccess(true);
//        } catch (Exception e) {
//            System.out.println("預約失敗訊息：" + e.getMessage());
//            result.setStatus("fail");
//            result.setReason(e.getMessage());
//            response.setSuccess(false);
//        }
//        results.add(result);
//        response.setResults(results);
//        return ResponseEntity.ok(response);
//    }
//
//    // 批量預約
//    @Operation(summary = "批量預約多本書籍")
//    @PostMapping("/batch")
//    @CheckJwt
//    public ResponseEntity<ReservationResponseDTO> batchReservation(@RequestBody ReservationBatchRequestDTO batchDto, @RequestHeader("Authorization") String authHeader) {
//        ReservationResponseDTO response = new ReservationResponseDTO();
//
//        // 生成統一的批次預約編號
//        String batchReservationId = "BATCH_" + System.currentTimeMillis();
//
//        // 添加調試日誌
//        System.out.println("=== 開始批量預約 ===");
//        System.out.println("批次ID: " + batchReservationId);
//        System.out.println("請求時間: " + java.time.LocalDateTime.now());
//
//        List<ReservationResponseDTO.Result> results = new ArrayList<>();
//        List<ReservationEntity> successfulReservations = new ArrayList<>();
//        boolean allSuccess = true;
//
//        try {
//            // 從 JWT token 獲取真實的用戶 ID
//            Integer userId = getUserIdFromToken(authHeader);
//            batchDto.setUserId(userId);
//
//            System.out.println("用戶ID: " + userId);
//            System.out.println("預約書籍數量: " + batchDto.getBooks().size());
//
//        for (ReservationBatchRequestDTO.BookReserveItem item : batchDto.getBooks()) {
//            ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
//            result.setBookId(item.getBookId());
//            try {
//                System.out.println("處理書籍ID: " + item.getBookId());
//
//                ReservationDTO dto = new ReservationDTO();
//                dto.setBookId(item.getBookId());
//                    dto.setUserId(userId);
//                    dto.setStatus(ReservationEntity.STATUS_PENDING);
//                    if (item.getReserveTime() == null) {
//                        throw new RuntimeException("資料缺失");
//                    }
//                dto.setReserveTime(java.time.LocalDateTime.parse(item.getReserveTime()));
//                dto.setBatchId(batchReservationId);
//
//                    // 設定取書相關資訊
//                    dto.setPickupLocation(batchDto.getPickupLocation() != null ? batchDto.getPickupLocation() : "一樓服務台");
//                    dto.setPickupMethod(batchDto.getPickupMethod() != null ? batchDto.getPickupMethod() : "親自取書");
//
//                ReservationEntity entity = reservationService.createReservation(dto, false);
//                result.setReservationId(entity.getReservationId());
//                result.setStatus("success");
//
//                    // 收集成功的預約
//                    successfulReservations.add(entity);
//                    System.out.println("書籍ID " + item.getBookId() + " 預約成功，預約ID: " + entity.getReservationId());
//
//                } catch (java.time.format.DateTimeParseException e) {
//                    System.out.println("預約失敗訊息：時間格式錯誤");
//                    result.setStatus("fail");
//                    result.setReason("資料缺失");
//                    allSuccess = false;
//                } catch (NullPointerException e) {
//                    System.out.println("預約失敗訊息：欄位為空");
//                    result.setStatus("fail");
//                    result.setReason("資料缺失");
//                    allSuccess = false;
//            } catch (Exception e) {
//                    System.out.println("預約失敗訊息：" + e.getMessage());
//                result.setStatus("fail");
//                result.setReason(e.getMessage());
//                allSuccess = false;
//                e.printStackTrace();
//            }
//            results.add(result);
//        }
//
//        response.setSuccess(allSuccess);
//        response.setResults(results);
//        response.setBatchReservationId(batchReservationId); // 回傳統一編號
//
//        System.out.println("批量預約處理完成");
//        System.out.println("成功預約數量: " + successfulReservations.size());
//        System.out.println("總預約數量: " + batchDto.getBooks().size());
//
//            // 如果有成功的預約，發送批量通知郵件
//            if (!successfulReservations.isEmpty()) {
//                System.out.println("準備發送批量通知郵件...");
//                try {
//                    // 根據 userId 查找會員資訊
//                    Member member = memberRepository.findById(userId).orElse(null);
//                    if (member != null) {
//                        notificationService.sendBatchReservationSuccessEmail(member, successfulReservations, batchReservationId);
//                        System.out.println("批量通知郵件發送完成");
//                    } else {
//                        System.out.println("找不到會員資訊，無法發送郵件");
//                    }
//                } catch (Exception e) {
//                    // 郵件發送失敗不影響預約流程，只記錄錯誤
//                    System.err.println("發送批量預約成功通知郵件失敗：" + e.getMessage());
//                }
//            } else {
//                System.out.println("沒有成功的預約，不發送郵件");
//            }
//
//        } catch (Exception e) {
//            System.out.println("批量預約失敗：" + e.getMessage());
//            response.setSuccess(false);
//        }
//
//        System.out.println("=== 批量預約結束 ===\n");
//        return ResponseEntity.ok(response);
//    }
//
//    // 取消預約
//    @Operation(summary = "取消單筆預約")
//    @DeleteMapping("/{reservationId}")
//    @CheckJwt
//    public ResponseEntity<?> deleteReservation(@PathVariable Integer reservationId, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查預約是否屬於當前用戶
//            ReservationEntity reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
//
//            if (!reservation.getUserId().equals(userId)) {
//                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限取消此預約"));
//            }
//
//            reservationService.deleteReservation(reservationId);
//            return ResponseEntity.ok(Map.of("success", true, "message", "預約已取消"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 取消預約 API
//    @Operation(summary = "將預約狀態設為取消")
//    @PutMapping("/{reservationId}/cancel")
//    @CheckJwt
//    public ResponseEntity<?> cancelReservation(@PathVariable Integer reservationId, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查預約是否屬於當前用戶
//            ReservationEntity reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new RuntimeException("找不到預約記錄"));
//
//            if (!reservation.getUserId().equals(userId)) {
//                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限取消此預約"));
//            }
//
//            reservationService.cancelReservation(reservationId);
//            return ResponseEntity.ok(Map.of("success", true, "message", "預約已取消"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 狀態轉換
//    @Operation(summary = "更新預約狀態")
//    @PutMapping("/{reservationId}/status")
//    @CheckJwt
//    public ResponseEntity<?> updateReservationStatus(@PathVariable Integer reservationId, @RequestBody ReservationDTO dto, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//        return reservationRepository.findById(reservationId)
//                .map(reservation -> {
//                        // 檢查預約是否屬於當前用戶
//                        if (!reservation.getUserId().equals(userId)) {
//                            throw new RuntimeException("無權限更新此預約");
//                        }
//
//                    reservation.setStatus(dto.getStatus());
//                    reservation.setUpdatedAt(LocalDateTime.now());
//                    ReservationEntity updated = reservationRepository.save(reservation);
//                    return ResponseEntity.ok(reservationService.convertToDTO(updated));
//                })
//                .orElse(ResponseEntity.notFound().build());
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 批量刪除預約
//    @Operation(summary = "批量刪除預約紀錄")
//    @DeleteMapping("/batch")
//    @CheckJwt
//    public ResponseEntity<?> batchDeleteReservations(@RequestBody BatchDeleteRequest request, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查所有預約是否屬於當前用戶
//            for (Integer reservationId : request.getReservationIds()) {
//                ReservationEntity reservation = reservationRepository.findById(reservationId)
//                    .orElseThrow(() -> new RuntimeException("找不到預約記錄 ID: " + reservationId));
//
//                if (!reservation.getUserId().equals(userId)) {
//                    return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限取消預約 ID: " + reservationId));
//                }
//            }
//
//            Map<String, Object> result = reservationService.batchCancelReservations(request.getReservationIds());
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 批量取消預約
//    @Operation(summary = "批量取消預約紀錄")
//    @PutMapping("/batch/cancel")
//    @CheckJwt
//    public ResponseEntity<?> batchCancelReservations(@RequestBody BatchDeleteRequest request, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查所有預約是否屬於當前用戶
//            for (Integer reservationId : request.getReservationIds()) {
//                ReservationEntity reservation = reservationRepository.findById(reservationId)
//                    .orElseThrow(() -> new RuntimeException("找不到預約記錄 ID: " + reservationId));
//
//                if (!reservation.getUserId().equals(userId)) {
//                    return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限取消預約 ID: " + reservationId));
//                }
//            }
//
//            Map<String, Object> result = reservationService.batchCancelReservations(request.getReservationIds());
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 新的批量取消預約 API
//    @Operation(summary = "新的批量取消預約 API")
//    @PutMapping("/batch-cancel")
//    @CheckJwt
//    public ResponseEntity<?> batchCancelReservationsNew(@RequestBody BatchCancelRequest request, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查所有預約是否屬於當前用戶
//            for (Integer reservationId : request.getReservationIds()) {
//                ReservationEntity reservation = reservationRepository.findById(reservationId)
//                    .orElseThrow(() -> new RuntimeException("找不到預約記錄 ID: " + reservationId));
//
//                if (!reservation.getUserId().equals(userId)) {
//                    return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限取消預約 ID: " + reservationId));
//                }
//            }
//
//            Map<String, Object> result = reservationService.batchCancelReservations(request.getReservationIds());
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
//        }
//    }
//
//    // 批量刪除請求 DTO
//    public static class BatchDeleteRequest {
//        private List<Integer> reservationIds;
//        public List<Integer> getReservationIds() { return reservationIds; }
//        public void setReservationIds(List<Integer> reservationIds) { this.reservationIds = reservationIds; }
//    }
//
//    // 批量取消請求 DTO
//    public static class BatchCancelRequest {
//        private List<Integer> reservationIds;
//        public List<Integer> getReservationIds() { return reservationIds; }
//        public void setReservationIds(List<Integer> reservationIds) { this.reservationIds = reservationIds; }
//    }
//
//    // 新增：預約歷史查詢 API
//    @Operation(summary = "查詢預約歷史紀錄")
//    @GetMapping("/history")
//    @CheckJwt
//    public ResponseEntity<List<ReservationHistoryDTO>> getReservationHistory(
//        @RequestParam(required = false, defaultValue = "true") boolean includeCancelled,
//        @RequestHeader("Authorization") String authHeader
//    ) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//            List<ReservationHistoryDTO> history = reservationService.getReservationHistoryByUserId(userId.toString(), includeCancelled);
//                return ResponseEntity.ok(history);
//        } catch (Exception e) {
//            System.out.println("查詢預約歷史失敗：" + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @Operation(summary = "確認預約")
//    @PostMapping("/confirm")
//    @CheckJwt
//    public ResponseEntity<ApiResponse> confirmReservation(@RequestBody ReservationConfirmRequest request, @RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//
//            // 檢查預約日誌是否存在
//            Optional<ReservationLogEntity> logOpt = reservationLogService.getLogById(request.getLogId());
//            if (!logOpt.isPresent()) {
//                return ResponseEntity.badRequest()
//                    .body(new ApiResponse(false, "找不到預約日誌記錄"));
//            }
//
//            ReservationLogEntity log = logOpt.get();
//
//            // 檢查使用者身份
//            if (!log.getUserId().equals(userId)) {
//                return ResponseEntity.status(403).body(new ApiResponse(false, "無權限確認此預約"));
//            }
//
//            // 檢查書籍是否一致
//            if (!log.getBook().getBookId().equals(request.getBookId())) {
//                return ResponseEntity.badRequest()
//                    .body(new ApiResponse(false, "書籍資訊不符"));
//            }
//
//            // 檢查狀態是否為 PENDING
//            if (!"PENDING".equals(log.getStatus())) {
//                return ResponseEntity.badRequest()
//                    .body(new ApiResponse(false, "此預約已被處理"));
//            }
//
//            // 建立正式預約記錄（不發送郵件，因為這可能是重複確認）
//            ReservationEntity reservation = reservationService.createReservation(log, false);
//
//            // 更新預約日誌狀態
//            reservationLogService.updateLogStatus(log, "CONFIRMED");
//
//            // 建立回應
//            ApiResponse response = new ApiResponse(true, "預約確認成功");
//            response.setReservationId(reservation.getReservationId());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new ApiResponse(false, "確認預約失敗：" + e.getMessage()));
//        }
//    }
//
//    // 新增：查詢用戶預約統計資訊
//    @Operation(summary = "查詢用戶預約統計資訊")
//    @GetMapping("/stats")
//    @CheckJwt
//    public ResponseEntity<Map<String, Object>> getUserReservationStats(@RequestHeader("Authorization") String authHeader) {
//        try {
//            Integer userId = getUserIdFromToken(authHeader);
//            Map<String, Object> stats = reservationService.getUserReservationStats(userId);
//            return ResponseEntity.ok(stats);
//        } catch (Exception e) {
//            System.out.println("查詢預約統計失敗：" + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//}
