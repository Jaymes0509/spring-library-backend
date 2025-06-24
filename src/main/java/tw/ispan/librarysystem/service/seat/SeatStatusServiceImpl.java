package tw.ispan.librarysystem.service.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.seat.SeatStatus;
import tw.ispan.librarysystem.repository.seat.SeatStatusRepository;

import java.util.List;

@Service
public class SeatStatusServiceImpl implements SeatStatusService {

    @Autowired
    private SeatStatusRepository repository;

    @Override
    public List<SeatStatus> getAllStatuses() {
        return repository.findAll();
    }
}
