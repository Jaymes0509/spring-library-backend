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
            b.bookId, b.title, b.author,
            bd.imgUrl,
            c.cName,
            null,
            COUNT(r.reservationId),
            bd.summary
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN b.bookDetail bd
        LEFT JOIN ReservationEntity r ON b.bookId = r.book.bookId AND r.reserveStatus = 1
        GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
        ORDER BY COUNT(r.reservationId) DESC, b.bookId ASC
    """)
    List<TopRankingsBookDto> findTopRankingsByReservations(Pageable pageable);

    // 📘 借閱排行榜：首頁總覽用（無條件）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author,
            bd.imgUrl,
            c.cName,
            null,
            COUNT(br.borrowId),
            bd.summary
        )
        FROM tw.ispan.librarysystem.entity.borrow.Borrow br
        JOIN br.book b
        JOIN b.category c
        LEFT JOIN b.bookDetail bd
        GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
        ORDER BY COUNT(br.borrowId) DESC, b.bookId ASC
    """)
    List<TopRankingsBookDto> findTopRankingsByBorrows(Pageable pageable);

    // 📙 評分排行榜：首頁總覽用（無條件 + 貝式平均）
    @Query("""
        SELECT new tw.ispan.librarysystem.dto.rank.TopRankingsBookDto(
            b.bookId, b.title, b.author,
            bd.imgUrl,
            c.cName,
            CAST((SUM(cmt.rating) + :m * :c) AS double) / (COUNT(cmt.commentId) + :m),
            COUNT(cmt.commentId),
            bd.summary
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN b.bookDetail bd
        LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
        GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
        HAVING COUNT(cmt.commentId) >= :minReviewCount AND COUNT(cmt.commentId) > 0
        ORDER BY 
            CAST((SUM(cmt.rating) + :m * :c) AS double) / (COUNT(cmt.commentId) + :m) DESC,
            COUNT(cmt.commentId) DESC,
            b.bookId ASC
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
        b.bookId, b.title, b.author,
        bd.imgUrl,
        c.cName,
        null,
        COUNT(r.reservationId),
        bd.summary
    )
    FROM BookEntity b
    JOIN b.category c
    LEFT JOIN b.bookDetail bd
    LEFT JOIN ReservationEntity r ON r.book.bookId = b.bookId AND r.reserveStatus = 1
    WHERE (:categoryId IS NULL OR c.cId = :categoryId)
      AND (:year IS NULL OR FUNCTION('YEAR', r.createdAt) = :year)
      AND (:month IS NULL OR FUNCTION('MONTH', r.createdAt) = :month)
      AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
    GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
    HAVING COUNT(r.reservationId) > 0
    ORDER BY COUNT(r.reservationId) DESC, b.bookId ASC
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
            b.bookId, b.title, b.author,
            bd.imgUrl,
            c.cName,
            null,
            COUNT(br.borrowId),
            bd.summary
        )
        FROM tw.ispan.librarysystem.entity.borrow.Borrow br
        JOIN br.book b
        JOIN b.category c
        LEFT JOIN b.bookDetail bd
        WHERE (:categoryId IS NULL OR c.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', br.borrowDate) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', br.borrowDate) = :month)
          AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
        GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
        ORDER BY COUNT(br.borrowId) DESC, b.bookId ASC
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
            b.bookId, b.title, b.author,
            bd.imgUrl,
            c.cName,
            CASE
                WHEN (COUNT(cmt.commentId) + :m) > 0
                THEN (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m)
                ELSE 0.0
            END,
            COUNT(cmt.commentId),
            bd.summary
        )
        FROM BookEntity b
        JOIN b.category c
        LEFT JOIN b.bookDetail bd
        LEFT JOIN BookComment cmt ON cmt.bookId = b.bookId
        WHERE (:categoryId IS NULL OR c.cId = :categoryId)
          AND (:year IS NULL OR FUNCTION('YEAR', cmt.createdAt) = :year)
          AND (:month IS NULL OR FUNCTION('MONTH', cmt.createdAt) = :month)
          AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
        GROUP BY b.bookId, b.title, b.author, c.cName, bd.imgUrl, bd.summary
        HAVING COUNT(cmt.commentId) >= :minReviewCount AND COUNT(cmt.commentId) > 0
        ORDER BY 
            CASE
                WHEN (COUNT(cmt.commentId) + :m) > 0
                THEN (1.0 * (SUM(cmt.rating) + :m * :c)) / (COUNT(cmt.commentId) + :m)
                ELSE 0.0
            END DESC,
            COUNT(cmt.commentId) DESC,
            b.bookId ASC
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
