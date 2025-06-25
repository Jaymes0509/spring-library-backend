package tw.ispan.librarysystem.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.service.reservation.ReservationLogService;
import tw.ispan.librarysystem.entity.reservation.ReservationLogEntity;
import tw.ispan.librarysystem.dto.reservation.ReservationLogDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationLogBatchDeleteRequest;
import tw.ispan.librarysystem.security.CheckJwt;
import tw.ispan.librarysystem.util.JwtTool;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import tw.ispan.librarysystem.repository.books.BookRepository;
import tw.ispan.librarysystem.entity.books.BookEntity;

@Tag(name = "預約清單管理", description = "提供預約清單的新增、查詢、移除、批量移除等功能")
@RestController
@RequestMapping("/api/reservation-logs")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationLogController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationLogController.class);

    @Autowired
    private ReservationLogService reservationLogService;

    @Autowired
    private BookRepository bookRepository;

    // 從 JWT token 獲取用戶 ID 的輔助方法
    private Integer getUserIdFromToken(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = JwtTool.parseUserIdFromToken(token);
            return userId.intValue();
        } catch (Exception e) {
            throw new RuntimeException("無法從 token 獲取用戶資訊：" + e.getMessage());
        }
    }

    @Operation(summary = "新增預約清單的書籍")
    @PostMapping
    @CheckJwt
    public ResponseEntity<Map<String, Object>> addReservationLog(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到預約清單請求: {}", request);
            
            // 從 JWT token 獲取用戶 ID
            Integer userId = getUserIdFromToken(authHeader);
            
            // 檢查必要欄位
            if (!request.containsKey("book_id") || 
                !request.containsKey("action") || !request.containsKey("status")) {
                throw new IllegalArgumentException("缺少必要欄位");
            }

            // 從請求中獲取資料並進行類型轉換
            Long bookId = null;
            try {
                bookId = Long.valueOf(request.get("book_id").toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("book_id 格式不正確");
            }

            String action = request.get("action").toString();
            String status = request.get("status").toString();

            logger.info("處理預約清單: bookId={}, userId={}, action={}, status={}", 
                       bookId, userId, action, status);

            // 建立預約清單
            ReservationLogEntity log = reservationLogService.createLog(bookId, userId, action, status);

            // 設置回應
            response.put("success", true);
            response.put("message", "成功加入預約清單");
            response.put("log_id", log.getId());
            
            logger.info("預約清單建立成功: {}", log.getId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("請求參數錯誤: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("處理預約清單時發生錯誤", e);
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "查詢使用者的預約清單")
    @GetMapping
    @CheckJwt
    public ResponseEntity<List<ReservationLogDTO>> getLogsByUserId(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            
            // 查詢該 userId 的所有 reservation_logs
            List<ReservationLogEntity> logs = reservationLogService.getLogsByUserId(userId);
            List<ReservationLogDTO> result = logs.stream().map(log -> {
                ReservationLogDTO dto = new ReservationLogDTO();
                dto.setLogId(log.getId());
                dto.setUserId(log.getUserId().longValue());
                dto.setBookId(log.getBookId());
                dto.setAction(log.getAction());
                dto.setStatus(log.getStatus());
                dto.setMessage(log.getMessage());
                dto.setCreatedAt(log.getCreatedAt());
                dto.setReserveTime(log.getReserveTime());
                // 查書名、作者
                BookEntity book = bookRepository.findById(log.getBookId().intValue()).orElse(null);
                if (book != null) {
                    logger.info("logId={}, bookId={}, isbn={}", log.getId(), book.getBookId(), book.getIsbn());
                    dto.setTitle(book.getTitle());
                    dto.setAuthor(book.getAuthor());
                    dto.setIsbn(book.getIsbn());
                }
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("查詢預約清單失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 移除單個預約清單項目
     * @param logId 清單ID
     * @return 移除結果
     */
    @Operation(summary = "移除單筆預約清單項目")
    @DeleteMapping("/{logId}")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> deleteLogById(@PathVariable Long logId, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到移除預約清單請求: logId={}", logId);
            
            // 從 JWT token 獲取用戶 ID
            Integer userId = getUserIdFromToken(authHeader);
            
            // 檢查預約清單是否屬於當前用戶
            ReservationLogEntity log = reservationLogService.getLogById(logId).orElse(null);
            if (log == null) {
                response.put("success", false);
                response.put("message", "找不到指定的預約清單");
                logger.warn("找不到指定的預約清單: logId={}", logId);
                return ResponseEntity.notFound().build();
            }
            
            if (!log.getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "無權限移除此預約清單");
                logger.warn("無權限移除預約清單: logId={}, userId={}, tokenUserId={}", logId, log.getUserId(), userId);
                return ResponseEntity.status(403).body(response);
            }
            
            boolean deleted = reservationLogService.deleteLogById(logId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "預約清單移除成功");
                logger.info("預約清單移除成功: logId={}", logId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "移除失敗");
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (Exception e) {
            logger.error("移除預約清單時發生錯誤: logId={}, error={}", logId, e.getMessage());
            response.put("success", false);
            response.put("message", "移除失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 批量移除預約清單項目
     * @param request 包含要移除的清單ID列表
     * @return 移除結果
     */
    @Operation(summary = "批量移除預約清單項目")
    @DeleteMapping("/batch")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> batchDeleteLogs(@RequestBody ReservationLogBatchDeleteRequest request, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到批量移除預約清單請求: logIds={}", request.getLogIds());
            
            // 從 JWT token 獲取用戶 ID
            Integer userId = getUserIdFromToken(authHeader);
            
            if (request.getLogIds() == null || request.getLogIds().isEmpty()) {
                response.put("success", false);
                response.put("message", "請提供要移除的清單ID列表");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 檢查所有預約清單是否屬於當前用戶
            for (Long logId : request.getLogIds()) {
                ReservationLogEntity log = reservationLogService.getLogById(logId).orElse(null);
                if (log != null && !log.getUserId().equals(userId)) {
                    response.put("success", false);
                    response.put("message", "無權限移除預約清單 ID: " + logId);
                    return ResponseEntity.status(403).body(response);
                }
            }
            
            int deletedCount = reservationLogService.deleteLogsByIds(request.getLogIds());
            
            response.put("success", true);
            response.put("message", String.format("成功移除 %d 筆預約清單", deletedCount));
            response.put("deletedCount", deletedCount);
            response.put("totalRequested", request.getLogIds().size());
            
            logger.info("批量移除預約清單完成: 請求移除 {} 筆，實際移除 {} 筆", 
                       request.getLogIds().size(), deletedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量移除預約清單時發生錯誤: error={}", e.getMessage());
            response.put("success", false);
            response.put("message", "批量移除失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
