package tw.ispan.librarysystem.service.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.repository.seat.SeatRepository;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository repository;

    @Override
    public List<Seat> getAllStatuses() {
        return repository.findAll();
    }
}
