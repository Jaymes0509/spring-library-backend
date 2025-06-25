// 預約日誌服務層，處理預約日誌相關的商業邏輯
package tw.ispan.librarysystem.service.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.reservation.ReservationLogRequest;
import tw.ispan.librarysystem.entity.reservation.ReservationLogEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.repository.reservation.ReservationLogRepository;
import tw.ispan.librarysystem.repository.books.BookRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationLogService {

    @Autowired
    private ReservationLogRepository reservationLogRepository;
    
    @Autowired
    private BookRepository bookRepository;

    public ReservationLogEntity createLog(Long bookId, Integer userId, String action, String status) {
        // 1. 檢查書籍是否存在
        BookEntity book = bookRepository.findById(bookId.intValue())
            .orElseThrow(() -> new RuntimeException("書籍不存在"));

        // 2. 檢查是否已有相同的預約
        if (reservationLogRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, "PENDING")) {
            throw new RuntimeException("已預約過");
        }

        // 3. 建立新的預約日誌
        ReservationLogEntity log = new ReservationLogEntity();
        log.setBookId(bookId);
        log.setUserId(userId);
        log.setAction(action);
        log.setStatus(status);
        log.setCreatedAt(LocalDateTime.now());
        log.setReserveTime(LocalDateTime.now());
        log.setBook(book);

        return reservationLogRepository.save(log);
    }

    public List<ReservationLogEntity> getLogsByUserId(Integer userId) {
        return reservationLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<ReservationLogEntity> getLogById(Long logId) {
        return reservationLogRepository.findById(logId);
    }

    public void updateLogStatus(ReservationLogEntity log, String status) {
        log.setStatus(status);
        reservationLogRepository.save(log);
    }

    // 移除舊的方法，因為已經被新的方法取代
    @Deprecated
    public ReservationLogEntity createReservationLog(ReservationLogRequest request) {
        return createLog(request.getBookId().longValue(), request.getUserId(), "CREATE", "PENDING");
    }
    
    @Deprecated
    public List<ReservationLogEntity> getUserLogs(Long userId) {
        return getLogsByUserId(userId.intValue());
    }

    /**
     * 刪除單個預約日誌
     * @param logId 日誌ID
     * @return 是否刪除成功
     */
    public boolean deleteLogById(Long logId) {
        try {
            if (reservationLogRepository.existsById(logId)) {
                reservationLogRepository.deleteById(logId);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("資料缺失");
        }
    }

    /**
     * 批量刪除預約日誌
     * @param logIds 日誌ID列表
     * @return 刪除成功的數量
     */
    public int deleteLogsByIds(List<Long> logIds) {
        try {
            int deletedCount = 0;
            for (Long logId : logIds) {
                if (reservationLogRepository.existsById(logId)) {
                    reservationLogRepository.deleteById(logId);
                    deletedCount++;
                }
            }
            return deletedCount;
        } catch (Exception e) {
            throw new RuntimeException("資料缺失");
        }
    }
} 