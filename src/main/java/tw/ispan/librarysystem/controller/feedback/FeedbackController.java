package tw.ispan.librarysystem.controller.feedback;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.feedback.FeedbackFormDto;

import jakarta.servlet.http.HttpSession;
import tw.ispan.librarysystem.dto.feedback.FeedbackReplyDto;
import tw.ispan.librarysystem.dto.feedback.FeedbackSummaryDto;
import tw.ispan.librarysystem.entity.feedback.Feedback;
import tw.ispan.librarysystem.repository.feedback.FeedbackRepository;
import tw.ispan.librarysystem.service.feedback.FeedbackService;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // ✅ 根據 Nuxt 前端實際網址修改
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackService feedbackService; // 加入服務層

    @PostMapping
    public ResponseEntity<String> submitFeedback(@Valid @RequestBody FeedbackFormDto form, HttpSession session) {
        // 驗證驗證碼
        String expectedCaptcha = (String) session.getAttribute("captcha");
        if (expectedCaptcha == null || !expectedCaptcha.equalsIgnoreCase(form.getCaptcha())) {
            System.out.println("驗證失敗❌");
            System.out.println("使用者輸入的驗證碼：" + form.getCaptcha());
            System.out.println("Session 中的正確驗證碼：" + expectedCaptcha);
            return ResponseEntity.badRequest().body("驗證碼錯誤");
        }

        // 建立 Entity 並儲存
        Feedback feedback = new Feedback();
        feedback.setName(form.getName());
        feedback.setCardNumber(form.getCardNumber());
        feedback.setPhone(form.getPhone());
        feedback.setEmail(form.getEmail());
        feedback.setSubject(form.getSubject());
        feedback.setContent(form.getContent());

        feedbackRepository.save(feedback);

        return ResponseEntity.ok("留言已成功送出");
    }

    // 管理員回覆
    @PostMapping("/reply")
    public ResponseEntity<String> replyToFeedback(@Valid @RequestBody FeedbackReplyDto dto) {
        boolean success = feedbackService.replyToFeedback(dto);
        if (success) {
            return ResponseEntity.ok("回覆成功");
        } else {
            return ResponseEntity.badRequest().body("找不到該留言");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeedbackSummaryDto>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<FeedbackSummaryDto> dtoList = feedbackList.stream().map(feedback -> {
            FeedbackSummaryDto dto = new FeedbackSummaryDto();
            dto.setId(feedback.getId());
            dto.setName(feedback.getName());
            dto.setSubject(feedback.getSubject());
            dto.setContent(feedback.getContent());
            dto.setStatus(feedback.getStatus().toString());
            dto.setReply(feedback.getReply());
            dto.setRepliedAt(feedback.getRepliedAt());
            dto.setCreatedAt(feedback.getCreatedAt());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtoList);
    }


}
