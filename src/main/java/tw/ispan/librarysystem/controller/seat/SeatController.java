package tw.ispan.librarysystem.controller.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.repository.seat.SeatRepository;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatRepository seatRepo;

    @GetMapping
    public List<Seat> getAll() {
        return seatRepo.findAll();
    }
}

