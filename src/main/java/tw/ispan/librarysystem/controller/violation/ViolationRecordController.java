package tw.ispan.librarysystem.controller.violation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.violation.ViolationRecord;
import tw.ispan.librarysystem.service.violation.ViolationRecordService;
import tw.ispan.librarysystem.dto.violation.ViolationRecordDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/violations")
@CrossOrigin(origins = "http://localhost:8080")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService service;

    @GetMapping("/user/{userId}")
    public List<ViolationRecord> getUserViolations(@PathVariable Integer userId) {
        return service.getViolationsByUserId(userId);
    }

    @GetMapping("/user/{userId}/suspended")
    public boolean isUserSuspended(@PathVariable Integer userId) {
        return service.isUserSuspended(userId);
    }

    /**
     * 獲取所有違規記錄（管理員功能）
     * @return 包含用戶資訊的違規記錄列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllViolations() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ViolationRecordDTO> violations = service.getAllViolations();
            
            response.put("success", true);
            response.put("message", "成功獲取違規記錄");
            response.put("data", violations);
            response.put("totalCount", violations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取違規記錄失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 獲取活躍的違規記錄（懲罰期尚未結束）
     * @return 活躍的違規記錄列表
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveViolations() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ViolationRecordDTO> violations = service.getActiveViolations();
            
            response.put("success", true);
            response.put("message", "成功獲取活躍違規記錄");
            response.put("data", violations);
            response.put("totalCount", violations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取活躍違規記錄失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 