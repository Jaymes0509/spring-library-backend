package tw.ispan.librarysystem.repository.reservation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {

    @EntityGraph(attributePaths = {"book", "book.category", "book.category.categorysystem"})
    @Override
    @NonNull
    List<ReservationEntity> findAll();

    @Query("SELECT new tw.ispan.librarysystem.dto.reservation.ReservationDTO(r.reservationId, r.status, b.title, r.reserveTime) " +
           "FROM ReservationEntity r JOIN r.book b")
    List<ReservationDTO> findReservationsWithBookTitle();

    @EntityGraph(attributePaths = {"book", "book.category", "book.category.categorysystem"})
    List<ReservationEntity> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"book", "book.category", "book.category.categorysystem"})
    List<ReservationEntity> findByBookBookId(Integer bookId);
}
