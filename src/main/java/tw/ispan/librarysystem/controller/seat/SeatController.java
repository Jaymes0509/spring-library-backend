package tw.ispan.librarysystem.controller.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.repository.seat.SeatReservationRepository;
import tw.ispan.librarysystem.repository.seat.SeatRepository;
import tw.ispan.librarysystem.security.CheckJwt;
import tw.ispan.librarysystem.service.seat.SeatService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatStatusService;

    @Autowired
    private SeatRepository seatRepository;

    //Spring 才能注入 reservationRepo(下面的/mark-available/{label}要用)
    @Autowired
    private SeatReservationRepository reservationRepo;


    // 查詢所有座位狀態
    @GetMapping("/status")
    public List<Seat> getStatuses() {
        return seatStatusService.getAllStatuses();
    }

    // 管理員標記座位損壞
    @PutMapping("/mark-broken/{label}")
    @CheckJwt
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> markSeatAsBroken(@PathVariable String label) {
        Optional<Seat> optional = seatRepository.findBySeatLabel(label);
        if (optional.isPresent()) {
            Seat seat = optional.get();
            seat.setStatus(Seat.Status.BROKEN);
            seatRepository.save(seat);
            return ResponseEntity.ok("✅ 座位已標記為損壞");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //管理員恢復成可用座位
    @PutMapping("/mark-available/{label}")
    @CheckJwt
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> markSeatAsAvailable(@PathVariable String label) {
        Optional<Seat> optional = seatRepository.findBySeatLabel(label);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Seat seat = optional.get();

        // 檢查是否還有未來的預約（今天或之後）
        Integer seatId = seat.getId();
        boolean hasUpcomingReservation = reservationRepo.existsBySeatIdAndDateFromToday(seatId, SeatReservation.Status.RESERVED);
        if (hasUpcomingReservation) {
            return ResponseEntity.status(409).body("❌ 此座位已有預約，無法恢復為可用");
        }

        seat.setStatus(Seat.Status.AVAILABLE);
        seatRepository.save(seat);
        return ResponseEntity.ok("✅ 座位已恢復為可用");
    }


}

