package tw.ispan.librarysystem.repository.manager.books;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerBookRepository
                extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity> {

        Optional<BookEntity> findByIsbn(String isbn);

        // 🔸 為 Specification 查詢加上 EntityGraph，解決 lazy loading 問題
        @Override
        @EntityGraph(attributePaths = { "category", "category.categorysystem" })
        Page<BookEntity> findAll(Specification<BookEntity> spec, Pageable pageable);

        // 基本搜尋
        List<BookEntity> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

        // 進階多條件搜尋，支援出版年區間與多選語言
        @Query(value = "SELECT b FROM BookEntity b WHERE " +
                        "(:title           IS NULL OR LOWER(b.title)       LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
                        "(:author          IS NULL OR LOWER(b.author)      LIKE LOWER(CONCAT('%', :author, '%'))) AND "
                        +
                        "(:publisher       IS NULL OR LOWER(b.publisher)   LIKE LOWER(CONCAT('%', :publisher, '%'))) AND "
                        +
                        "(:isbn            IS NULL OR b.isbn               LIKE CONCAT('%', :isbn, '%')) AND " +
                        "(:classification  IS NULL OR b.classification     = :classification) AND " +
                        "(:yearFrom       IS NULL OR b.publishdate >= :yearFrom) AND " +
                        "(:yearTo         IS NULL OR b.publishdate <= :yearTo) AND " +
                        "(:language        IS NULL OR b.language          = :language)")
        Page<BookEntity> searchBooks(
                        @Param("title") String title,
                        @Param("author") String author,
                        @Param("publisher") String publisher,
                        @Param("isbn") String isbn,
                        @Param("classification") String classification,
                        @Param("yearFrom") Integer yearFrom,
                        @Param("yearTo") Integer yearTo,
                        @Param("language") String language,
                        Pageable pageable);
}