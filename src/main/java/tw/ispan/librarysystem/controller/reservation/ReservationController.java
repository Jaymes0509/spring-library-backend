// @SuppressWarnings("SpellCheckingInspection")
package tw.ispan.librarysystem.controller.reservation;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 查詢用戶預約清單
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
            return ResponseEntity.internalServerError().build();
        }
    }

    // 查詢單筆預約
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer reservationId) {
        return reservationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 查詢某本書的所有預約
    @GetMapping("/book/{bookId}")
    public List<ReservationDTO> getReservationsByBookId(@PathVariable Integer bookId) {
        return reservationService.getReservationsByBookId(bookId);
    }

    // 單本預約
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationDTO dto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
        result.setBookId(dto.getBookId());
        try {
            ReservationEntity entity = reservationService.createReservation(dto);
            result.setReservationId(entity.getReservationId());
            result.setStatus("success");
            response.setSuccess(true);
        } catch (Exception e) {
            result.setStatus("fail");
            result.setReason(e.getMessage());
            response.setSuccess(false);
        }
        results.add(result);
        response.setResults(results);
        return ResponseEntity.ok(response);
    }

    // 批量預約
    @PostMapping("/batch")
    public ResponseEntity<ReservationResponseDTO> batchReservation(@RequestBody ReservationBatchRequestDTO batchDto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        
        // 生成統一的批次預約編號
        String batchReservationId = "BATCH_" + System.currentTimeMillis();
        
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        boolean allSuccess = true;
        
        for (ReservationBatchRequestDTO.BookReserveItem item : batchDto.getBooks()) {
            ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
            result.setBookId(item.getBookId());
            try {
                ReservationDTO dto = new ReservationDTO();
                dto.setBookId(item.getBookId());
                dto.setUserId(batchDto.getUserId());
                dto.setStatus("PENDING");
                dto.setReserveTime(java.time.LocalDateTime.parse(item.getReserveTime()));
                
                // 設定統一的批次編號到每筆預約記錄
                dto.setBatchId(batchReservationId);
                
                ReservationEntity entity = reservationService.createReservation(dto);
                result.setReservationId(entity.getReservationId());
                result.setStatus("success");
            } catch (Exception e) {
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
        try {
            System.out.println("批量預約回傳內容：" + new ObjectMapper().writeValueAsString(response));
        } catch (Exception e) {
            System.out.println("回傳內容序列化失敗：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // 取消預約
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 狀態轉換
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
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteReservations(@RequestBody BatchDeleteRequest request) {
        try {
            reservationRepository.deleteAllById(request.getReservationIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("批量刪除失敗：" + e.getMessage());
        }
    }

    // 批量刪除請求 DTO
    public static class BatchDeleteRequest {
        private List<Integer> reservationIds;
        public List<Integer> getReservationIds() { return reservationIds; }
        public void setReservationIds(List<Integer> reservationIds) { this.reservationIds = reservationIds; }
    }

    // 新增：預約歷史查詢 API
    @GetMapping("/history")
    public ResponseEntity<List<ReservationHistoryDTO>> getReservationHistory(
        @RequestParam(required = false) String userId
    ) {
        try {
            if (userId != null) {
                // 查詢特定會員的預約歷史
                List<ReservationHistoryDTO> history = reservationService.getReservationHistoryByUserId(userId);
                return ResponseEntity.ok(history);
            } else {
                // 查詢所有預約歷史 (管理員功能)
                List<ReservationHistoryDTO> history = reservationService.getAllReservationHistory();
                return ResponseEntity.ok(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

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