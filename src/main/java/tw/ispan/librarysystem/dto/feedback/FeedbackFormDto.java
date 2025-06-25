package tw.ispan.librarysystem.dto.feedback;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FeedbackFormDto {

    @NotBlank(message = "姓名不能為空")
    private String name;

    private String cardNumber;

    @NotBlank(message = "電話不能為空")
    private String phone;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "主旨不能為空")
    private String subject;

    @NotBlank(message = "內容不能為空")
    @Size(max = 1000, message = "內容不能超過 1000 字")
    private String content;

    @NotBlank(message = "請輸入驗證碼")
    private String captcha;

	}

