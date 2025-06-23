package tw.ispan.librarysystem.entity.reservation;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import tw.ispan.librarysystem.entity.books.BookEntity;

@Data
@Entity
@Table(name = "reservation_logs")
public class ReservationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "book_id")
    private Long bookId;
    
    @Column(name = "action")
    private String action;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "reserve_time", nullable = true)
    private LocalDateTime reserveTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book;
    
    // Getter 方法
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getBookId() {
        return bookId;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getReserveTime() {
        return reserveTime;
    }
    
    public BookEntity getBook() {
        return book;
    }
    
    // Setter 方法
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setReserveTime(LocalDateTime reserveTime) {
        this.reserveTime = reserveTime;
    }
    
    public void setBook(BookEntity book) {
        this.book = book;
    }
}