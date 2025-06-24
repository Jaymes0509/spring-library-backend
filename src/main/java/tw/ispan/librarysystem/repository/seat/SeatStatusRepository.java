package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.ispan.librarysystem.entity.seat.SeatStatus;

import java.util.Optional;

public interface SeatStatusRepository extends JpaRepository<SeatStatus, Long> {
    Optional<SeatStatus> findBySeatLabel(String seatLabel);
}

