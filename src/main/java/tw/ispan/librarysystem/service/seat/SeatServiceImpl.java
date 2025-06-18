package tw.ispan.librarysystem.service.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.enums.SeatStatus;
import tw.ispan.librarysystem.repository.seat.SeatRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepo;

//    @Autowired
//    private SeatReservationRepository reservationRepo;

    @Override
    public List<Seat> getAllSeats() {
        return seatRepo.findAll();
    }

//    @Override
//    public List<Seat> getAvailableSeats() {
//        return seatRepo.findByStatus(SeatStatus.AVAILABLE);
//    }
//
//    @Override
//    public List<Seat> getAvailableSeatsByDateAndTime(LocalDate date, String timeSlot) {
//        // 查出當天該時段已被預約的 seatId
//        List<Long> reservedSeatIds = reservationRepo.findReservedSeatIds(date, timeSlot);
//
//        // 回傳 status = AVAILABLE 且不在 reserved 中的座位
//        return seatRepo.findByStatus(SeatStatus.AVAILABLE)
//                .stream()
//                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
//                .collect(Collectors.toList());
//    }
}

