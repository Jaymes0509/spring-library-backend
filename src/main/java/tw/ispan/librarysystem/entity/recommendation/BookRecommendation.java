package tw.ispan.librarysystem.entity.recommendation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "book_recommendations")
public class BookRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "br_id")
    private Long id;

    private String title;
    private String isbn;
    private String author;
    private String publisher;
    private Integer publishYear;


    @NotBlank
    private String reason;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
