package tw.ispan.librarysystem.service.comment;

import tw.ispan.librarysystem.dto.comment.TakeCommentBookInfoDto;
import tw.ispan.librarysystem.entity.comment.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {

    List<TakeCommentBookInfoDto> findReviewableBooks(Integer userId);

    BookComment saveComment(BookComment comment);

    Optional<BookComment> findCommentByBookIdAndUserId(Integer bookId, Integer userId);

    List<BookComment> findCommentsByBookId(Integer bookId);

    void deleteCommentById(Integer commentId);

    Optional<BookComment> findCommentById(Integer commentId);

    List<BookComment> findCommentsByUserId(Integer userId);

    // 新增宣告：
    List<TakeCommentBookInfoDto> findAllBorrowedBooks(Integer userId);
}
