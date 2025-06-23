package tw.ispan.librarysystem.service.seat;

import tw.ispan.librarysystem.entity.seat.SeatStatus;

import java.util.List;

public interface SeatStatusService {
    List<SeatStatus> getAllStatuses();
}
