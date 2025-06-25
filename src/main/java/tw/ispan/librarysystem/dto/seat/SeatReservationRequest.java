package tw.ispan.librarysystem.dto.seat;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class SeatReservationRequest {

    private Integer userId;             // 使用者 ID（可從登入資訊帶入）
    private String seatLabel;           // 座位標籤，如 A1、B5（對應 seat_status 的 s_label）
    private LocalDate reservationDate;  // 預約日期
    private String timeSlot;            // 預約時段，如 "09:00 - 11:00"
}

