package tw.ispan.librarysystem.repository.rank;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.entity.books.BookEntity;

import java.util.List;

public interface TopRankingsRepository extends JpaRepository<BookEntity, Integer> {

    // 📗 預約排行榜：首頁用（無條件）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author, '', c.cName, null, COUNT(r.reservationId) * 1L
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN ReservationEntity r ON b.bookId = r.book.bookId AND r.reserveStatus = 1
        GROUP BY b.bookId, b.title, b.author, c.cName
        ORDER BY COUNT(r.reservationId) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByReservations(Pageable pageable);

    // 📘 借閱排行榜：首頁用（條件式查詢）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author, '', c.cName, null, COUNT(br.borrowId)
        )
        FROM tw.ispan.librarysystem.entity.borrow.Borrow br
        JOIN br.book b
        JOIN b.category c
        WHERE (:categoryId IS NULL OR c.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', br.createdAt) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', br.createdAt) = :month)
          AND br.status = 'RETURNED'
        GROUP BY b.bookId, b.title, b.author, c.cName
        ORDER BY COUNT(br.borrowId) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByBorrows(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    // 📙 評分排行榜（貝式平均）：首頁用
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author, '', c.cName,
            (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m),
            COUNT(cmt.commentId)
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
        GROUP BY b.bookId, b.title, b.author, c.cName
        HAVING COUNT(cmt.commentId) >= :minReviewCount
        ORDER BY (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m) DESC
    """)
    List<TopRankingsBookDto> findTopRankingsByRatings(
            @Param("m") double m,
            @Param("c") double c,
            @Param("minReviewCount") long minReviewCount,
            Pageable pageable
    );

    // 🔍 預約詳細搜尋（含條件）
    // 📘 預約排行榜：詳細頁用（支援條件 categoryId / year / month / keyword）
    @Query("""
    SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
        b.bookId, b.title, b.author, '', c.cName, null, COUNT(r.reservationId) * 1L
    )
    FROM BookEntity b
    JOIN b.category c
    LEFT JOIN ReservationEntity r ON r.book.bookId = b.bookId AND r.reserveStatus = 1
    WHERE (:categoryId IS NULL OR c.cId = :categoryId)
      AND (:year IS NULL OR FUNCTION('YEAR', r.createdAt) = :year)
      AND (:month IS NULL OR FUNCTION('MONTH', r.createdAt) = :month)
      AND (:keyword IS NULL OR b.title LIKE %:keyword%)
    GROUP BY b.bookId, b.title, b.author, c.cName
    ORDER BY COUNT(r.reservationId) DESC
""")
    List<TopRankingsBookDto> findTopRankingsByReservationCondition(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 🔍 借閱詳細搜尋（含條件）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author, '', c.cName, null, COUNT(br.borrowId)
        )
        FROM tw.ispan.librarysystem.entity.borrow.Borrow br
        JOIN br.book b
        JOIN b.category c
        WHERE (:categoryId IS NULL OR c.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', br.createdAt) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', br.createdAt) = :month)
          AND (:keyword IS NULL OR b.title LIKE %:keyword%)
          AND br.status = 'RETURNED'
        GROUP BY b.bookId, b.title, b.author, c.cName
        ORDER BY COUNT(br.borrowId) DESC
    """)
    List<TopRankingsBookDto> findDetailedBorrows(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 🔍 評分詳細搜尋（含條件 + 貝式平均）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author, '', c.cName,
            (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m),
            COUNT(cmt.commentId)
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
        WHERE (:categoryId IS NULL OR c.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', cmt.createdAt) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', cmt.createdAt) = :month)
          AND (:keyword IS NULL OR b.title LIKE %:keyword%)
        GROUP BY b.bookId, b.title, b.author, c.cName
        HAVING COUNT(cmt.commentId) >= :minReviewCount
        ORDER BY (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m) DESC
    """)
    List<TopRankingsBookDto> findDetailedRatings(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("keyword") String keyword,
            @Param("m") double m,
            @Param("c") double c,
            @Param("minReviewCount") long minReviewCount,
            Pageable pageable
    );
}
