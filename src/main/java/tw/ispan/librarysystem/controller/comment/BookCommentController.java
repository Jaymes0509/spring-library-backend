package tw.ispan.librarysystem.controller.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.comment.BookComment;
import tw.ispan.librarysystem.service.comment.BookCommentService;
import tw.ispan.librarysystem.dto.comment.TakeCommentBookInfoDto;
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
        comment.setCommentId(null); // 確保是新增
        BookComment saved = bookCommentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 更新書評（PUT）
    @PutMapping("/{commentId}")
    public ResponseEntity<BookComment> updateComment(@PathVariable Integer commentId, @RequestBody BookComment comment) {
        Optional<BookComment> existing = bookCommentService.findCommentById(commentId);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
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

    // 查詢某使用者對某書的書評（檢查是否已評論）
    @GetMapping("/book/{bookId}/user/{userId}")
    public ResponseEntity<BookComment> getCommentByUserAndBook(@PathVariable Integer bookId, @PathVariable Integer userId) {
        Optional<BookComment> comment = bookCommentService.findCommentByBookIdAndUserId(bookId, userId);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 查詢某書所有書評
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookComment>> getCommentsByBookId(@PathVariable Integer bookId) {
        List<BookComment> comments = bookCommentService.findCommentsByBookId(bookId);
        return ResponseEntity.ok(comments);
    }

    // 取得可撰寫書評的書籍清單
    @GetMapping("/reviewable-books/{userId}")
    public ResponseEntity<List<TakeCommentBookInfoDto>> getReviewableBooks(@PathVariable Integer userId) {
        List<TakeCommentBookInfoDto> books = bookCommentService.findReviewableBooks(userId);
        return ResponseEntity.ok(books);
    }

    // 取得用戶所有書評（新增此方法以取得用戶的書評列表）
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookComment>> getCommentsByUserId(@PathVariable Integer userId) {
        List<BookComment> comments = bookCommentService.findCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    // 取得用戶所有借閱書籍（含書名作者）
    @GetMapping("/borrowed-books/{userId}")
    public ResponseEntity<List<TakeCommentBookInfoDto>> getAllBorrowedBooks(@PathVariable Integer userId) {
        List<TakeCommentBookInfoDto> books = bookCommentService.findAllBorrowedBooks(userId);
        return ResponseEntity.ok(books);
    }

}
