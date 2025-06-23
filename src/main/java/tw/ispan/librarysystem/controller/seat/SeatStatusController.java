package tw.ispan.librarysystem.controller.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.seat.SeatStatus;
import tw.ispan.librarysystem.repository.seat.SeatStatusRepository;
import tw.ispan.librarysystem.service.seat.SeatStatusService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats")
public class SeatStatusController {

    @Autowired
    private SeatStatusService seatStatusService;

    @Autowired
    private SeatStatusRepository seatStatusRepository;

    // 查詢所有座位狀態
    @GetMapping("/status")
    public List<SeatStatus> getStatuses() {
        return seatStatusService.getAllStatuses();
    }

    @PutMapping("/reserve/{label}")
    public ResponseEntity<String> reserveSeat(@PathVariable String label) {
        Optional<SeatStatus> optional = seatStatusRepository.findBySeatLabel(label);
        if (optional.isPresent()) {
            SeatStatus seat = optional.get();
            if (seat.getStatus() == SeatStatus.Status.AVAILABLE) {
                seat.setStatus(SeatStatus.Status.RESERVED);
                seatStatusRepository.save(seat);
                return ResponseEntity.ok("預約成功");
            } else {
                return ResponseEntity.status(409).body("❌ 該座位已被預約或無法使用");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

