package tw.ispan.librarysystem.controller.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.seat.SeatReservationRequest;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.enums.TimeSlot;
import tw.ispan.librarysystem.repository.seat.SeatRepository;
import tw.ispan.librarysystem.repository.seat.SeatReservationRepository;
import tw.ispan.librarysystem.service.seat.SeatReservationService;
import tw.ispan.librarysystem.exception.SeatAlreadyReservedException;
import tw.ispan.librarysystem.exception.UserAlreadyReservedException;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import org.springframework.web.bind.annotation.RequestBody;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats/reservations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SeatReservationController {

    @Autowired
    private SeatReservationService seatReservationService;

    @Autowired
    private SeatReservationRepository reservationRepo;

    @Autowired
    private SeatRepository seatRepo;


    //查詢當日已被預約的座位(根據時間與時段找出預約座位標籤)
    @GetMapping("/occupied")
    public List<String> getReservedSeats(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam TimeSlot timeSlot
    ) {
        return seatReservationService.getReservedSeatLabels(date, timeSlot);
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookSeat(@RequestBody SeatReservationRequest request) {
        System.out.println("📥 收到預約請求：" + request);

        try {
            String result = seatReservationService.reserveSeat(request); //預約邏輯寫入 reservation 表

            Optional<Seat> optionalSeat = seatRepo.findBySeatLabel(request.getSeatLabel());
            if (optionalSeat.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ 座位不存在");
            }

            return ResponseEntity.ok(result); //  最終只有這個 return, 預約成功

        } catch (SeatAlreadyReservedException e) {
            // 該座位已被預約
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ 該座位已被預約");
        } catch (UserAlreadyReservedException e) {
            // 使用者已預約過此時段
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ 您已預約同一時段的");

        } catch (IllegalArgumentException e) {
            // 傳入參數錯誤（如找不到座位標籤）
            return ResponseEntity.badRequest().body("❌ 錯誤請求：" + e.getMessage());

        } catch (Exception e) {
            // 其他未知錯誤
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 系統錯誤，請稍後再試");
        }


    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUserReserved(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String timeSlot
    ) {
        TimeSlot slot = TimeSlot.fromLabel(timeSlot);
        boolean exists = reservationRepo.existsByUserIdAndReservationDateAndTimeSlotAndStatus(
                userId, date, slot, SeatReservation.Status.RESERVED
        );
        return ResponseEntity.ok(exists);
    }


    @PutMapping("/cancel")
    public ResponseEntity<String> cancelByUser(
            @RequestParam Integer userId,
            @RequestParam String seatLabel,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("timeSlot") String timeSlotStr
    ) {
        try {
            TimeSlot timeSlot = TimeSlot.fromLabel(timeSlotStr);
            boolean cancelled = seatReservationService.cancelReservationByUser(userId, seatLabel, date, timeSlot);

            if (cancelled) {
                return ResponseEntity.ok("✅ 預約已取消");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 找不到要取消的預約");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ 無效的時段：" + timeSlotStr);
        }
    }

    // 查詢有未來預約的座位
    @GetMapping("/reservations/upcoming")
    public List<String> getUpcomingSeatLabels() {
        return reservationRepo.findUpcomingSeatLabels();
    }
}


