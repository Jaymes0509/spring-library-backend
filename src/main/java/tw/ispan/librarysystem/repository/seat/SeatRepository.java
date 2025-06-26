package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.ispan.librarysystem.entity.seat.Seat;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findBySeatLabel(String seatLabel);
}

