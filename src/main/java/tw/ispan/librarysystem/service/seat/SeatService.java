package tw.ispan.librarysystem.service.seat;

import tw.ispan.librarysystem.entity.seat.Seat;

import java.time.LocalDate;
import java.util.List;

public interface SeatService {
    List<Seat> getAllSeats(); // 所有座位（無論狀態)

    //List<Seat> getAvailableSeats(); // 查看status = available

    //List<Seat> getAvailableSeatsByDateAndTime(LocalDate date, String timeSlot); // 特定時間未被預約的可用座位
}
