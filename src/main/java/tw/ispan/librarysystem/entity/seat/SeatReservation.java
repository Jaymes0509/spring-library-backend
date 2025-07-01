package tw.ispan.librarysystem.entity.seat;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tw.ispan.librarysystem.enums.TimeSlot;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seat_reservations")
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_id")
    private Long srId;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "s_id")
    private Seat seat;

    @Column(name = "reservation_date")
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false)
    private TimeSlot timeSlot;

    public enum Status {
        RESERVED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    private Status status;  // RESERVED or CANCELLED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



}




