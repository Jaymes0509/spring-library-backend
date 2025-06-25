package tw.ispan.librarysystem.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRecommendationDto {
    @NotBlank(message = "書名不能為空")
    private String title;

    @NotBlank(message = "ISBN不能為空")
    private String isbn;
    private String author;
    private String publisher;
    private Integer publishYear;

    @Size(max = 1000, message = "內容不能超過 1000 字")
    private String reason;

    @NotBlank(message = "請輸入驗證碼")
    private String captcha;

}

