package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.enums.SeatStatus;
import tw.ispan.librarysystem.enums.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    List<SeatReservation> findByReservationDateAndTimeSlotAndStatus(
            LocalDate reservationDate, TimeSlot timeSlot, SeatReservation.Status status
    );

//    該使用者是否已在同一日期同一時段預約過座位
    boolean existsByUserIdAndReservationDateAndTimeSlotAndStatus(
            Integer userId, LocalDate reservationDate, TimeSlot timeSlot, SeatReservation.Status status);
    // 要排除 CANCELLED 狀態, 是否有非取消狀態的預約（例如：RESERVED）
//    boolean existsByUserIdAndReservationDateAndTimeSlotAndStatusNot(
//            Integer userId,
//            LocalDate reservationDate,
//            TimeSlot timeSlot,
//            SeatReservation.Status status
//    );


    boolean existsBySeatAndReservationDateAndTimeSlotAndStatus(
            Seat seat, LocalDate date, TimeSlot timeSlot, SeatReservation.Status status
    );

    List<SeatReservation> findBySeatAndReservationDateAndTimeSlotAndStatus(
            SeatStatus seat,
            LocalDate reservationDate,
            TimeSlot timeSlot,
            SeatReservation.Status status
    );

    Optional<SeatReservation> findByUserIdAndReservationDateAndTimeSlotAndStatus(
            Integer userId,
            LocalDate reservationDate,
            TimeSlot timeSlot,
            SeatReservation.Status status
    );

    // 查詢使用者未來一筆預約（給「我的座位預約」用）
    Optional<SeatReservation> findFirstByUserIdAndReservationDateAfterAndStatusOrderByReservationDateAsc(
            Integer userId, LocalDate today, SeatReservation.Status status
    );



    // 可以在排程中找出所有過期但還是 RESERVED 的預約，進行取消。
    List<SeatReservation> findByReservationDateBeforeAndStatus(LocalDate date, SeatReservation.Status status);

    // 查詢某使用者的全部預約紀錄（我的預約功能）
    List<SeatReservation> findByUserId(Integer userId);

    // 查詢單筆預約以便進行取消
    SeatReservation findBySeat_IdAndReservationDateAndTimeSlot(
            Integer seatId, LocalDate date, TimeSlot slot);

    //這個查詢會檢查：是否有「指定座位」的預約，日期是今天或未來,  確保 SeatReservation entity 裡有 seat（多對一關聯）跟 reservationDate 欄位，否則這查詢會錯。
    @Query("SELECT COUNT(r) > 0 FROM SeatReservation r WHERE r.seat.id = :seatId AND r.reservationDate >= CURRENT_DATE AND r.status = :status")
    boolean existsBySeatIdAndDateFromToday(@Param("seatId") Integer seatId, @Param("status") SeatReservation.Status status);

    //查詢有未來預約的座位
    @Query("SELECT DISTINCT r.seat.seatLabel FROM SeatReservation r WHERE r.reservationDate >= CURRENT_DATE AND r.status = :status")
    List<String> findUpcomingSeatLabels(@Param("status") SeatReservation.Status status);

    // 查詢今日含以後的所有預約
    @Query("SELECT r FROM SeatReservation r WHERE r.userId = :userId AND r.reservationDate >= :today AND r.status = :status ORDER BY r.reservationDate ASC")
    List<SeatReservation> findAllUpcomingByUserId(
            @Param("userId") Integer userId,
            @Param("today") LocalDate today,
            @Param("status") SeatReservation.Status status
    );


}

