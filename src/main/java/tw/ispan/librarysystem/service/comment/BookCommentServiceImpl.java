package tw.ispan.librarysystem.service.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.comment.BookComment;
import tw.ispan.librarysystem.repository.comment.BookCommentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookCommentServiceImpl implements BookCommentService {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Override
    public BookComment saveComment(BookComment comment) {
        return bookCommentRepository.save(comment);
    }

    @Override
    public Optional<BookComment> findCommentByBookIdAndUserId(Integer bookId, Integer userId) {
        return bookCommentRepository.findByBookIdAndUserId(bookId, userId);
    }

    @Override
    public List<BookComment> findCommentsByBookId(Integer bookId) {
        return bookCommentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    @Override
    public void deleteCommentById(Integer commentId) {
        bookCommentRepository.deleteById(commentId);
    }

    @Override
    public Optional<BookComment> findCommentById(Integer commentId) {
        return bookCommentRepository.findById(commentId);
    }
}
