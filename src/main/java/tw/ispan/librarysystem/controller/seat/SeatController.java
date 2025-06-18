package tw.ispan.librarysystem.controller.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.service.seat.SeatService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

//    @GetMapping("/available")
//    public List<Seat> getAvailableSeats() {
//        return seatService.getAvailableSeats();
//    }
//
//    @GetMapping("/available/search")
//    public List<Seat> getAvailableByDateAndTime(
//        @RequestParam("date") String dateStr,
//        @RequestParam("timeSlot") String timeSlot
//    ) {
//        LocalDate date = LocalDate.parse(dateStr);
//        return seatService.getAvailableSeatsByDateAndTime(date, timeSlot);
//    }

}

