package tw.ispan.librarysystem.repository.rank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.entity.books.BookEntity;

import java.util.List;

public interface TopRankingsRepository extends JpaRepository<BookEntity, Integer> {

    // 📗 預約排行榜：首頁總覽用（無條件）
    @Query("""
            SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
                b.bookId, b.title, b.author, '', c.cName, null, CAST(COUNT(r.reservationId) AS long)
            )
            FROM BookEntity b
            JOIN b.category c
            LEFT JOIN ReservationEntity r ON b.bookId = r.book.bookId AND r.reserveStatus = 1
            GROUP BY b.bookId, b.title, b.author, c.cName
            ORDER BY COUNT(r.reservationId) DESC
        """)
    List<TopRankingsBookDto> findTopRankingsByReservations(Pageable pageable);

    // 📘 借閱排行榜：首頁總覽用（無條件）
    @Query("""
            SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
                b.bookId, b.title, b.author, '', c.cName, null, COUNT(br.borrowId)
            )
            FROM tw.ispan.librarysystem.entity.borrow.Borrow br
            JOIN br.book b
            JOIN b.category c
            WHERE br.status = 'RETURNED'
            GROUP BY b.bookId, b.title, b.author, c.cName
            ORDER BY COUNT(br.borrowId) DESC
        """)
    List<TopRankingsBookDto> findTopRankingsByBorrows(Pageable pageable);

    // 📙 評分排行榜：首頁總覽用（無條件 + 貝式平均）
    @Query("""
            SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
                b.bookId, b.title, b.author, '', c.cName,
                CAST((SUM(cmt.rating) + :m * :c) AS double) / (COUNT(cmt.commentId) + :m),
                COUNT(cmt.commentId)
            )
            FROM BookEntity b
            JOIN b.category c
            LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
            GROUP BY b.bookId, b.title, b.author, c.cName
            HAVING COUNT(cmt.commentId) >= :minReviewCount AND COUNT(cmt.commentId) > 0
            ORDER BY CAST((SUM(cmt.rating) + :m * :c) AS double) / (COUNT(cmt.commentId) + :m) DESC
        """)
    List<TopRankingsBookDto> findTopRankingsByRatings(
            @Param("m") double m,
            @Param("c") double c,
            @Param("minReviewCount") long minReviewCount,
            Pageable pageable
    );

    // 🔍 預約排行榜：詳細搜尋（有條件）
    @Query("""
    SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
        b.bookId, b.title, b.author, '', c.cName, null, CAST(COUNT(r.reservationId) AS long)
    )
    FROM BookEntity b
    JOIN b.category c
    LEFT JOIN ReservationEntity r ON r.book.bookId = b.bookId AND r.reserveStatus = 1
    WHERE (:categoryId IS NULL OR c.cId = :categoryId)
      AND (:year IS NULL OR FUNCTION('YEAR', r.createdAt) = :year)
      AND (:month IS NULL OR FUNCTION('MONTH', r.createdAt) = :month)
      AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
    GROUP BY b.bookId, b.title, b.author, c.cName
    HAVING COUNT(r.reservationId) > 0
    ORDER BY COUNT(r.reservationId) DESC
""")
    Page<TopRankingsBookDto> findTopRankingsByReservationCondition(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // 🔍 借閱排行榜：詳細搜尋（有條件）
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
              AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
              AND br.status = 'RETURNED'
            GROUP BY b.bookId, b.title, b.author, c.cName
            ORDER BY COUNT(br.borrowId) DESC
        """)
    Page<TopRankingsBookDto> findDetailedBorrows(
            @Param("categoryId") Integer categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 🔍 評分排行榜：詳細搜尋（有條件 + 貝式平均）
    @Query("""
            SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
                b.bookId, b.title, b.author, '', c.cName,
                CASE
                    WHEN (COUNT(cmt.commentId) + :m) > 0
                    THEN (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m)
                    ELSE 0.0
                END,
                COUNT(cmt.commentId)
            )
            FROM BookEntity b
            JOIN b.category c
            LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
            WHERE (:categoryId IS NULL OR c.cId = :categoryId)
              AND (:year IS NULL OR FUNCTION('YEAR', cmt.createdAt) = :year)
              AND (:month IS NULL OR FUNCTION('MONTH', cmt.createdAt) = :month)
              AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
            GROUP BY b.bookId, b.title, b.author, c.cName
            HAVING COUNT(cmt.commentId) >= :minReviewCount AND COUNT(cmt.commentId) > 0
            ORDER BY CASE
                         WHEN (COUNT(cmt.commentId) + :m) > 0
                         THEN (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m)
                         ELSE 0.0
                     END DESC
        """)
    Page<TopRankingsBookDto> findDetailedRatings(
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
