package tw.ispan.librarysystem.entity.feedback;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import tw.ispan.librarysystem.enums.FeedbackStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    private String name;

    @Column(nullable = true) // 可為 null
    private String cardNumber;

    private String phone;
    private String email;
    private String subject;

    @Column(length = 1000)
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 1000, nullable = true)
    private String reply;

    @Column(nullable = true)
    private LocalDateTime repliedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FeedbackStatus status = FeedbackStatus.待處理;



}
