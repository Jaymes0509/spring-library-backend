package tw.ispan.librarysystem.dto.borrow;

import lombok.Data;
import java.util.List;

@Data
public class BorrowBatchRequestDto {
    private Integer userId;
    private List<BorrowBookRequest> books;
    
    @Data
    public static class BorrowBookRequest {
        private Integer bookId;
        private Integer duration; // 借書期限（天）
        private String location; // 取書地點
        private String method;   // 借書方式
    }
} 