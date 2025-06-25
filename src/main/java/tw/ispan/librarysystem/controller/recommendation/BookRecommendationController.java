package tw.ispan.librarysystem.controller.recommendation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.recommendation.BookRecommendationDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;
import tw.ispan.librarysystem.security.CheckJwt;
import tw.ispan.librarysystem.service.recommendation.BookRecommendationService;

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

}

