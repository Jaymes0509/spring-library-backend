package tw.ispan.librarysystem.entity.seat;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "seat_status")
public class SeatStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_id")
    private Integer id;

    @Column(name = "s_label", unique = true)
    private String seatLabel;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        AVAILABLE, RESERVED, BROKEN
    }

}
