package tw.ispan.librarysystem.service.comment;

import tw.ispan.librarysystem.entity.comment.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {
    BookComment saveComment(BookComment comment);
    Optional<BookComment> findCommentByBookIdAndUserId(Integer bookId, Integer userId);
    List<BookComment> findCommentsByBookId(Integer bookId);
    void deleteCommentById(Integer commentId);

    Optional<BookComment> findCommentById(Integer commentId);
}
