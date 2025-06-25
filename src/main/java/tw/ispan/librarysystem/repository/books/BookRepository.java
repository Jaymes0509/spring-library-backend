package tw.ispan.librarysystem.repository.books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.dto.BookSimpleDTO;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer>,JpaSpecificationExecutor<BookEntity> {
    
    Optional<BookEntity> findByIsbn(String isbn);

    // 為 Specification 查詢加上 EntityGraph，解決 lazy loading 問題
    @Override
    Page<BookEntity> findAll(Specification<BookEntity> spec, Pageable pageable);
    
    @EntityGraph(attributePaths = {"category", "category.categorysystem", "bookDetail"})
    Optional<BookEntity> findById(Integer id);

    // 基本搜尋（查 title, author, isbn, imgUrl, publisher）
    @Query("SELECT new tw.ispan.librarysystem.dto.BookSimpleDTO(b.bookId, b.isbn, b.title, b.author, d.imgUrl, b.publisher, b.publishdate) FROM BookEntity b LEFT JOIN b.bookDetail d WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BookSimpleDTO> simpleSearchWithCover(@Param("keyword") String keyword, Pageable pageable);
    
    
    // 進階多條件搜尋（查 title, author, isbn, imgUrl, publisher, publishdate）
    @Query(value =
       "SELECT new tw.ispan.librarysystem.dto.BookSimpleDTO(b.bookId, b.isbn, b.title, b.author, d.imgUrl, b.publisher, b.publishdate) FROM BookEntity b LEFT JOIN b.bookDetail d WHERE " +
       "(:title           IS NULL OR LOWER(b.title)       LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
       "(:author          IS NULL OR LOWER(b.author)      LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
       "(:publisher       IS NULL OR LOWER(b.publisher)   LIKE LOWER(CONCAT('%', :publisher, '%'))) AND " +
       "(:isbn            IS NULL OR b.isbn               LIKE CONCAT('%', :isbn, '%')) AND " +
       "(:classification  IS NULL OR b.classification     LIKE CONCAT('%', :classification, '%')) AND " +
       "(:yearFrom       IS NULL OR b.publishdate >= :yearFrom) AND " +
       "(:yearTo         IS NULL OR b.publishdate <= :yearTo) AND " +
       "(:language        IS NULL OR b.language          = :language)"
    )
    Page<BookSimpleDTO> advancedSearchWithCover(
        @Param("title")          String title,
        @Param("author")         String author,
        @Param("publisher")      String publisher,
        @Param("isbn")           String isbn,
        @Param("classification") String classification,
        @Param("yearFrom")       Integer  yearFrom,
        @Param("yearTo")         Integer  yearTo,
        @Param("language")       String language,
        Pageable pageable
    );
}