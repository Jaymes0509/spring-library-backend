package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.entity.seat.SeatStatus;
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
            SeatStatus seat, LocalDate date, TimeSlot timeSlot, SeatReservation.Status status
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


    // 可以在排程中找出所有過期但還是 RESERVED 的預約，進行取消。
    List<SeatReservation> findByReservationDateBeforeAndStatus(LocalDate date, SeatReservation.Status status);

    // 查詢某使用者的全部預約紀錄（我的預約功能）
    List<SeatReservation> findByUserId(Integer userId);

    // 查詢單筆預約以便進行取消
    SeatReservation findBySeat_IdAndReservationDateAndTimeSlot(
            Integer seatId, LocalDate date, TimeSlot slot);
}

