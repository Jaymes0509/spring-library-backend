package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.dto.comment.SearchBookDTO;
import tw.ispan.librarysystem.entity.books.BookEntity;

@Repository
public interface SearchBookRepository extends JpaRepository<BookEntity, Integer> {

    @Query("""
                SELECT new tw.ispan.librarysystem.dto.comment.SearchBookDTO(
                    b.bookId, b.title, b.author, b.category.cName
                )
                FROM BookEntity b
                WHERE
                    (:categoryName IS NULL OR b.category.cName = :categoryName)
                AND
                    (
                        :keyword IS NULL OR :keyword = ''
                        OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            """)
    Page<SearchBookDTO> searchBooksByCategoryAndKeyword(
            @Param("categoryName") String categoryName,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
