package tw.ispan.librarysystem.entity.reservation;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import tw.ispan.librarysystem.entity.books.BookEntity;

@Entity
@Table(name = "reservations")  // 保持 reservations 表
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")  // 使用 book_id 關聯 BookEntity
    private BookEntity book;  // 與 BookEntity 關聯

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "reserve_time")
    private LocalDateTime reserveTime;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "status")
    private String status;  // PENDING, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(LocalDateTime reserveTime) {
        this.reserveTime = reserveTime;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ReservationEntity{" +
                "reservationId=" + reservationId +
                ", bookId=" + (book != null ? book.getBookId() : "null") +
                ", userId=" + userId +
                ", reserveTime=" + reserveTime +
                ", expiryDate=" + expiryDate +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
