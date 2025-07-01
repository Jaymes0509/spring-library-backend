package tw.ispan.librarysystem.controller.recommendation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.recommendation.BookRecommendationDto;
import tw.ispan.librarysystem.dto.recommendation.SimpleRecommendationDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;
import tw.ispan.librarysystem.security.CheckJwt;
import tw.ispan.librarysystem.service.recommendation.BookRecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class BookRecommendationController {
    private final BookRecommendationService service;

    @PostMapping
    @CheckJwt
    public ResponseEntity<?> submit(
            @RequestBody @Valid BookRecommendationDto dto,
            HttpServletRequest request) {

        Member member = (Member) request.getAttribute("user"); // 從 JwtAspect 放進來的會員
        // ✅ 測試用：手動建立一個 member
//        Member member = new Member();
//        member.setUserId(1L); // 用 1 號會員進行測試

        try {
            BookRecommendation saved = service.submitRecommendation(dto, member); // 傳入後端儲存
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/count")
    @CheckJwt
    public ResponseEntity<?> getCount(HttpServletRequest request) {
        Member member = (Member) request.getAttribute("user"); //    取得登入會員

        // ↓↓↓ 測試用會員資料（之後記得移除）
//        Member member = new Member();
//        member.setUserId(1L);
        return ResponseEntity.ok(service.getUserCount(member));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam BookRecommendation.Status status
    ) {
        service.updateStatus(id, status);
        return ResponseEntity.ok("狀態更新成功");
    }

    // 「查看自己推薦清單」的功能 API，讓使用者可以查自己曾推薦過哪些書。
    @GetMapping("/my-list")
    @CheckJwt
    public ResponseEntity<?> getMyRecommendations(HttpServletRequest request) {
        Member member = (Member) request.getAttribute("user"); // 透過 JWT 驗證後的使用者
        List<SimpleRecommendationDto> result = service.getUserRecommendations(member)
                .stream()
                .map(r -> new SimpleRecommendationDto(
                        r.getTitle(),
                        r.getIsbn(),
                        r.getReason(),
                        r.getStatus().name(), // 或轉中文
                        r.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}



