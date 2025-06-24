package tw.ispan.librarysystem.service.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.comment.TakeCommentBookInfoDto;
import tw.ispan.librarysystem.entity.comment.BookComment;
import tw.ispan.librarysystem.repository.comment.BookCommentRepository;
import tw.ispan.librarysystem.repository.comment.TakeCommentBookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookCommentServiceImpl implements BookCommentService {

    @Autowired
    private TakeCommentBookRepository bookRepository;

    @Autowired
    private BookCommentRepository commentRepository;

    @Override
    public List<TakeCommentBookInfoDto> findReviewableBooks(Integer userId) {
        return bookRepository.findBooksEligibleForReview(userId);
    }

    @Override
    public BookComment saveComment(BookComment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<BookComment> findCommentByBookIdAndUserId(Integer bookId, Integer userId) {
        return commentRepository.findByBookIdAndUserId(bookId, userId);
    }

    @Override
    public List<BookComment> findCommentsByBookId(Integer bookId) {
        return commentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    @Override
    public void deleteCommentById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public Optional<BookComment> findCommentById(Integer commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public List<BookComment> findCommentsByUserId(Integer userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<TakeCommentBookInfoDto> findAllBorrowedBooks(Integer userId) {
        return bookRepository.findAllBorrowedBooks(userId);
    }
}
