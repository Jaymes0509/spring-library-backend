package tw.ispan.librarysystem.controller.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.service.comment.LikeBookService;

@RestController
@RequestMapping("/api/comment")
public class LikeBookController {

    @Autowired
    private LikeBookService likeBookService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment(@PathVariable Integer commentId, @RequestParam Integer memberId) {
        boolean success = likeBookService.likeComment(commentId, memberId);
        if (success) {
            return ResponseEntity.ok("點讚成功");
        } else {
            return ResponseEntity.badRequest().body("您已點讚過此書評");
        }
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> unlikeComment(@PathVariable Integer commentId, @RequestParam Integer memberId) {
        boolean success = likeBookService.unlikeComment(commentId, memberId);
        if (success) {
            return ResponseEntity.ok("取消點讚成功");
        } else {
            return ResponseEntity.badRequest().body("您尚未對此書評點讚");
        }
    }
}
