package tw.ispan.librarysystem.service.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.ispan.librarysystem.dto.seat.SeatReservationRequest;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.entity.seat.SeatReservation;
import tw.ispan.librarysystem.entity.seat.SeatReservation.Status;
import tw.ispan.librarysystem.enums.TimeSlot;
import tw.ispan.librarysystem.exception.SeatAlreadyReservedException;
import tw.ispan.librarysystem.exception.UserAlreadyReservedException;
import tw.ispan.librarysystem.repository.seat.SeatReservationRepository;
import tw.ispan.librarysystem.repository.seat.SeatRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatReservationServiceImpl implements SeatReservationService {

    private final SeatReservationRepository reservationRepo;
    private final SeatRepository seatRepo;

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
        Seat seat = seatRepo.findBySeatLabel(request.getSeatLabel())
                .orElseThrow(() -> new IllegalArgumentException("找不到座位: " + request.getSeatLabel()));

        System.out.println("📥 預約請求內容：");
        System.out.println("📍 userId: " + request.getUserId());
        System.out.println("📍 seatLabel: " + request.getSeatLabel());
        System.out.println("📍 reservationDate: " + request.getReservationDate());
        System.out.println("📍 timeSlot: " + request.getTimeSlot());
        TimeSlot slot = TimeSlot.fromLabel(request.getTimeSlot()); // ✅ 轉為 Enum
        System.out.println("📍 轉換後的時段 enum: " + slot.name());

        //  先檢查是否有一筆相同資料但狀態是 CANCELLED
        Optional<SeatReservation> cancelledReservation = reservationRepo
                .findByUserIdAndReservationDateAndTimeSlotAndStatus(
                        request.getUserId(),
                        request.getReservationDate(),
                        slot,
                        SeatReservation.Status.CANCELLED
                );

        if (cancelledReservation.isPresent()) {
            //  恢復狀態為 RESERVED
            SeatReservation existing = cancelledReservation.get();
            existing.setSeat(seat); // 若座位變了則更新
            existing.setStatus(SeatReservation.Status.RESERVED);
            reservationRepo.save(existing);
            return "✅ 預約已重新啟用: " + seat.getSeatLabel();
        }


        // 這段為檢查同一人同一天同時段是否已預約
        boolean alreadyBooked = reservationRepo.existsByUserIdAndReservationDateAndTimeSlotAndStatus(
                request.getUserId(),
                request.getReservationDate(),
                slot,
                SeatReservation.Status.RESERVED
        );
        if (alreadyBooked) {
            throw new UserAlreadyReservedException("❌ 您已在該時段預約過其他座位");
        }

        // 這段為檢查座位是否被預約( 該座位是否已被其他人預約（狀態為 RESERVED）)
        boolean seatTaken = reservationRepo.existsBySeatAndReservationDateAndTimeSlotAndStatus(
                seat,
                request.getReservationDate(),
                slot,
                SeatReservation.Status.RESERVED
        );
        if (seatTaken) {
            throw new SeatAlreadyReservedException("❌ 該座位已被預約");
        }

        // 沒預約過,建立新預約
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
    public boolean cancelReservationByUser(Integer userId, String seatLabel, LocalDate date, TimeSlot timeSlot) {
        Seat seat = seatRepo.findBySeatLabel(seatLabel)
                .orElseThrow(() -> new IllegalArgumentException("找不到座位：" + seatLabel));

        Optional<SeatReservation> reservationOpt = reservationRepo
                .findByUserIdAndReservationDateAndTimeSlotAndStatus(
                        userId, date, timeSlot, Status.RESERVED
                );

        if (reservationOpt.isEmpty()) {
            return false;
        }

        SeatReservation reservation = reservationOpt.get();
        reservation.setStatus(Status.CANCELLED);
        reservationRepo.save(reservation);
        return true;

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
