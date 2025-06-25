package tw.ispan.librarysystem.repository.borrow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.borrow.Borrow;
import tw.ispan.librarysystem.entity.borrow.Borrow.BorrowStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    
    // 基本查詢
    List<Borrow> findByUserId(Long userId);
    List<Borrow> findByBookId(Integer bookId);
    List<Borrow> findByStatus(BorrowStatus status);
    
    // 分頁查詢
    Page<Borrow> findByUserId(Long userId, Pageable pageable);
    Page<Borrow> findByBookId(Integer bookId, Pageable pageable);
    Page<Borrow> findByStatus(BorrowStatus status, Pageable pageable);
    
    // 複合查詢
    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId AND b.status = :status")
    List<Borrow> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BorrowStatus status);
    
    @Query("SELECT b FROM Borrow b WHERE b.bookId = :bookId AND b.status = :status")
    List<Borrow> findByBookIdAndStatus(@Param("bookId") Integer bookId, @Param("status") BorrowStatus status);
    
    // 借閱歷史查詢
    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId ORDER BY b.borrowDate DESC")
    List<Borrow> findMemberBorrowHistory(@Param("userId") Long userId);
    
    // 逾期查詢
    @Query("SELECT b FROM Borrow b WHERE b.dueDate < :now AND b.status IN ('BORROWED', 'RENEWED')")
    List<Borrow> findOverdueBorrows(@Param("now") LocalDateTime now);
    
    // 統計查詢
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.userId = :userId AND b.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BorrowStatus status);
    
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.bookId = :bookId AND b.status = :status")
    long countByBookIdAndStatus(@Param("bookId") Integer bookId, @Param("status") BorrowStatus status);
    
    // 檢查是否有未歸還的借閱
    @Query("SELECT COUNT(b) > 0 FROM Borrow b WHERE b.userId = :userId AND b.bookId = :bookId AND b.status IN ('BORROWED', 'RENEWED')")
    boolean existsActiveBookBorrow(@Param("userId") Long userId, @Param("bookId") Integer bookId);
    
    // 新增：根據日期範圍查詢
    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId AND b.borrowDate BETWEEN :startDate AND :endDate ORDER BY b.borrowDate DESC")
    List<Borrow> findByUserIdAndBorrowDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // 新增：查詢即將到期的借閱（3天內）
    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId AND b.dueDate BETWEEN :now AND :dueDate AND b.status IN ('BORROWED', 'RENEWED')")
    List<Borrow> findUpcomingDueBorrows(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now, 
        @Param("dueDate") LocalDateTime dueDate
    );
    
    // 新增：查詢可續借的借閱
    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId AND b.status IN ('BORROWED', 'RENEWED') AND b.renewCount < 2 AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Borrow> findRenewableBorrows(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // 新增：統計用戶總借閱次數
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.userId = :userId")
    long countTotalBorrowsByUserId(@Param("userId") Long userId);
    
    // 新增：統計用戶總續借次數
    @Query("SELECT SUM(b.renewCount) FROM Borrow b WHERE b.userId = :userId")
    Long sumRenewCountByUserId(@Param("userId") Long userId);
    
    // 新增：查詢最常借閱的書籍
    @Query("SELECT b.bookId, COUNT(b) as borrowCount FROM Borrow b WHERE b.userId = :userId GROUP BY b.bookId ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedBooksByUserId(@Param("userId") Long userId);
} 