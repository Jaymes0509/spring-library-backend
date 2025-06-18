package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.ispan.librarysystem.entity.comment.RandomBook;

import java.util.List;

public interface RandomBookRepository extends JpaRepository<RandomBook, Integer> {

    @Query(value = "SELECT * FROM books ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<RandomBook> findRandomTenBooks();
}
