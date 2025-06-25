package tw.ispan.librarysystem.dto.borrow;

import lombok.Data;
import java.util.List;

@Data
public class BorrowBatchResponseDto {
    private boolean success;
    private String message;
    private List<BorrowResult> results;
    
    @Data
    public static class BorrowResult {
        private Integer bookId;
        private String status; // "success" 或 "failed"
        private Integer borrowId; // 成功時才有
        private String message; // 失敗時才有錯誤訊息
    }
} 