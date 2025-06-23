package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.entity.seat.SeatStatus;
import tw.ispan.librarysystem.enums.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    List<SeatReservation> findByReservationDateAndTimeSlotAndStatus(
            LocalDate reservationDate, TimeSlot timeSlot, SeatReservation.Status status
    );

    boolean existsBySeatAndReservationDateAndTimeSlotAndStatus(
            SeatStatus seat, LocalDate date, TimeSlot timeSlot, SeatReservation.Status status
    );

    List<SeatReservation> findBySeatAndReservationDateAndTimeSlotAndStatus(
            SeatStatus seat,
            LocalDate reservationDate,
            TimeSlot timeSlot,
            SeatReservation.Status status
    );

    // 可以在排程中找出所有過期但還是 RESERVED 的預約，進行取消。
    List<SeatReservation> findByReservationDateBeforeAndStatus(LocalDate date, SeatReservation.Status status);

}

