package tw.ispan.librarysystem.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tw.ispan.librarysystem.enums.FeedbackStatus;

@Data
public class FeedbackReplyDto {

    private Long id; // 必須有，讓後端知道是哪一筆 Feedback

    @NotBlank(message = "回覆內容不能為空")
    private String reply;

    @NotBlank(message = "狀態不能為空")
    private FeedbackStatus status; // 例如 "已回覆";
}

