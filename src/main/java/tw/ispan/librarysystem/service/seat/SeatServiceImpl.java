package tw.ispan.librarysystem.service.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.enums.SeatStatus;
import tw.ispan.librarysystem.repository.seat.SeatRepository;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepo;

    @Override
    public List<Seat> getAllSeats() {
        return seatRepo.findAll();
    }

    @Override
    public List<Seat> getAvailableSeats() {
        return seatRepo.findByStatus(SeatStatus.AVAILABLE);
    }
}

