package tw.ispan.librarysystem.dto.seat;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SeatReservationDto {
    private String seatLabel;           // 座位標籤
    private LocalDate reservationDate;  // 預約日期
    private String timeSlot;            // 時段字串，如 "09:00 - 11:00"
}

// 這是給前端查看預約資訊用的 DTO
