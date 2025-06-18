package tw.ispan.librarysystem.controller.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.comment.BookComment;
import tw.ispan.librarysystem.service.comment.BookCommentService;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/book-comments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookCommentController {

    @Autowired
    private BookCommentService bookCommentService;

    // 新增書評（POST）
    @PostMapping
    public ResponseEntity<BookComment> createComment(@RequestBody BookComment comment) {
        // 新增時不應帶有 commentId，若帶了可檢查或忽略
        comment.setCommentId(null); // 確保是新增
        BookComment saved = bookCommentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 更新書評（PUT）
    @PutMapping("/{commentId}")
    public ResponseEntity<BookComment> updateComment(@PathVariable Integer commentId, @RequestBody BookComment comment) {
        // 先檢查要更新的書評是否存在
        Optional<BookComment> existing = bookCommentService.findCommentById(commentId);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // 設定要更新的ID，防止錯誤
        comment.setCommentId(commentId);
        BookComment updated = bookCommentService.saveComment(comment);
        return ResponseEntity.ok(updated);
    }

    // 刪除書評
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        bookCommentService.deleteCommentById(commentId);
        return ResponseEntity.noContent().build();
    }

    // 查詢某使用者對某書的書評（用於檢查是否已評論）
    @GetMapping("/book/{bookId}/user/{userId}")
    public ResponseEntity<BookComment> getCommentByUserAndBook(@PathVariable Integer bookId, @PathVariable Integer userId) {
        Optional<BookComment> comment = bookCommentService.findCommentByBookIdAndUserId(bookId, userId);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookComment>> getCommentsByBookId(@PathVariable Integer bookId) {
        List<BookComment> comments = bookCommentService.findCommentsByBookId(bookId);
        return ResponseEntity.ok(comments);
    }

}
