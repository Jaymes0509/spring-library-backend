package tw.ispan.librarysystem.service.seat;

import tw.ispan.librarysystem.entity.seat.Seat;

import java.util.List;

public interface SeatService {
    List<Seat> getAllSeats();
    List<Seat> getAvailableSeats();
}
