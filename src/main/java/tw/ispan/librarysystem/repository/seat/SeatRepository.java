package tw.ispan.librarysystem.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.ispan.librarysystem.entity.seat.Seat;
import tw.ispan.librarysystem.enums.SeatStatus;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByStatus(SeatStatus status);
}
