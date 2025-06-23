package tw.ispan.librarysystem.repository.rank;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.entity.books.BookEntity;

import java.util.List;

public interface TopRankingsRepository extends JpaRepository<BookEntity, Integer> {

    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId,
            b.title,
            b.author,
            '',
            b.category.cName,
            0.0,
            COUNT(r.reservationId)
        )
        FROM BookEntity b
        JOIN ReservationEntity r ON r.book.bookId = b.bookId
        WHERE r.status = 'COMPLETED'
        GROUP BY b.bookId, b.title, b.author, b.category.cName
        ORDER BY COUNT(r.reservationId) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByReservations(Pageable pageable);

    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId,
            b.title,
            b.author,
            '',
            '',
            null,
            COUNT(br.borrowId)
        )
        FROM BookEntity b
        JOIN Borrow br ON br.book.bookId = b.bookId
        WHERE (:categoryId IS NULL OR b.category.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', br.borrowDate) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', br.borrowDate) = :month)
          AND br.status = 'RETURNED'
        GROUP BY b.bookId, b.title, b.author
        ORDER BY COUNT(br.borrowId) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByBorrows(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId,
            b.title,
            b.author,
            '',
            b.category.cName,
            ((:m * :c + AVG(c.rating) * COUNT(c)) / (:m + COUNT(c))),
            COUNT(c)
        )
        FROM BookEntity b
        JOIN BookComment c ON c.bookId = b.bookId
        GROUP BY b.bookId, b.title, b.author, b.category.cName
        HAVING COUNT(c) >= :minReviewCount
        ORDER BY ((:m * :c + AVG(c.rating) * COUNT(c)) / (:m + COUNT(c))) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByRatings(
            @Param("m") double m,
            @Param("c") double c,
            @Param("minReviewCount") long minReviewCount,
            Pageable pageable
    );
}
