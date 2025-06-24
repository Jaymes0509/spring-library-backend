package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.dto.comment.TakeCommentBookInfoDto;

import java.util.List;

@Repository
public interface TakeCommentBookRepository extends JpaRepository<BookEntity, Integer> {

    @Query(value = """
    SELECT DISTINCT b.book_id AS bookId, b.title, b.author
    FROM borrow_records r
    JOIN books b ON r.book_id = b.book_id
    WHERE r.user_id = :userId
      AND r.status = 'RETURNED'
      AND r.book_id NOT IN (
        SELECT book_id FROM bookcomments WHERE user_id = :userId
      )
""", nativeQuery = true)
    List<TakeCommentBookInfoDto> findBooksEligibleForReview(@Param("userId") Integer userId);


    @Query(value = """
    SELECT DISTINCT b.book_id AS bookId, b.title, b.author
    FROM borrow_records r
    JOIN books b ON r.book_id = b.book_id
    WHERE r.user_id = :userId
      AND r.status = 'RETURNED'
""", nativeQuery = true)
    List<TakeCommentBookInfoDto> findAllBorrowedBooks(@Param("userId") Integer userId);

}
