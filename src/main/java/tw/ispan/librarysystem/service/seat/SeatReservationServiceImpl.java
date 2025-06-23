package tw.ispan.librarysystem.service.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.ispan.librarysystem.dto.seat.SeatReservationRequest;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.entity.seat.SeatReservation.Status;
import tw.ispan.librarysystem.entity.seat.SeatStatus;
import tw.ispan.librarysystem.enums.TimeSlot;
import tw.ispan.librarysystem.repository.seat.SeatReservationRepository;
import tw.ispan.librarysystem.repository.seat.SeatStatusRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatReservationServiceImpl implements SeatReservationService {

    private final SeatReservationRepository reservationRepo;
    private final SeatStatusRepository seatStatusRepo;

    @Override
    public List<String> getReservedSeatLabels(LocalDate date, TimeSlot timeSlot) {
        return reservationRepo
                .findByReservationDateAndTimeSlotAndStatus(date, timeSlot, Status.RESERVED)
                .stream()
                .map(res -> res.getSeat().getSeatLabel())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String reserveSeat(SeatReservationRequest request) {
        SeatStatus seat = seatStatusRepo.findBySeatLabel(request.getSeatLabel())
                .orElseThrow(() -> new IllegalArgumentException("找不到座位: " + request.getSeatLabel()));

        System.out.println("📥 預約請求內容：");
        System.out.println("📍 userId: " + request.getUserId());
        System.out.println("📍 seatLabel: " + request.getSeatLabel());
        System.out.println("📍 reservationDate: " + request.getReservationDate());
        System.out.println("📍 timeSlot: " + request.getTimeSlot());
        TimeSlot slot = TimeSlot.fromLabel(request.getTimeSlot()); // ✅ 轉為 Enum
        System.out.println("📍 轉換後的時段 enum: " + slot.name());
        boolean exists = reservationRepo.existsBySeatAndReservationDateAndTimeSlotAndStatus(
                seat,
                request.getReservationDate(),
                slot,
                SeatReservation.Status.RESERVED
        );

        if (exists) {
            return "❌ 該座位已被預約";
        }

        SeatReservation reservation = new SeatReservation();
        reservation.setUserId(request.getUserId());
        reservation.setSeat(seat);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setTimeSlot(slot); // ✅ 使用 Enum
        reservation.setStatus(SeatReservation.Status.RESERVED);

        reservationRepo.save(reservation);
        return "✅ 預約成功: " + seat.getSeatLabel();
    }



    @Override
    @Transactional
    public String cancelReservation(String seatLabel, LocalDate date, TimeSlot timeSlot) {
        SeatStatus seat = seatStatusRepo.findBySeatLabel(seatLabel)
                .orElseThrow(() -> new IllegalArgumentException("找不到座位：" + seatLabel));

        List<SeatReservation> reservations = reservationRepo.findBySeatAndReservationDateAndTimeSlotAndStatus(
                seat, date, timeSlot, Status.RESERVED
        );

        if (reservations.isEmpty()) {
            return "❌ 找不到要取消的預約";
        }

        reservations.forEach(r -> r.setStatus(Status.CANCELLED));
        reservationRepo.saveAll(reservations);

        return "✅ 預約已取消";
    }

    @Override
    @Transactional
    public void cancelExpiredReservations() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<SeatReservation> allReserved = reservationRepo.findAll().stream()
                .filter(r -> r.getStatus() == Status.RESERVED)
                .filter(r -> {
                    // 比較時間是否已過期（可根據你的 timeSlot 格式判斷）
                    try {
                        TimeSlot slot = r.getTimeSlot(); // 回傳 Enum
                        String endTime = slot.getEnd(); // 不需要 split
                        LocalDateTime endDateTime = LocalDateTime.of(r.getReservationDate(), LocalTime.parse(endTime));
                        return endDateTime.isBefore(LocalDateTime.now());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        allReserved.forEach(r -> r.setStatus(Status.CANCELLED));
        reservationRepo.saveAll(allReserved);
    }
}
