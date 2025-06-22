package tw.ispan.librarysystem.service.seat;

import tw.ispan.librarysystem.dto.seat.SeatReservationRequest;
import tw.ispan.librarysystem.enums.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface SeatReservationService {

    // 查詢當天某時段已預約的座位（給前端用）
    List<String> getReservedSeatLabels(LocalDate date, TimeSlot timeSlot);

    // 預約座位（給前端用）
    String reserveSeat(SeatReservationRequest request);

    // 取消某筆預約（如需後台或用戶主動取消）
    String cancelReservation(String seatLabel, LocalDate date, TimeSlot timeSlot);

    // 自動取消過期預約（排程使用）
    void cancelExpiredReservations();
}

