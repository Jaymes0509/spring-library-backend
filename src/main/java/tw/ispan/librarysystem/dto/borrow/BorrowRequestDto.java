package tw.ispan.librarysystem.dto.borrow;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class BorrowRequestDto {
    @NotNull(message = "書籍ID不能為空")
    private Integer bookId;
} 