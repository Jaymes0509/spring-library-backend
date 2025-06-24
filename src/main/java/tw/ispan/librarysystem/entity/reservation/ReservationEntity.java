// 預約資料庫實體，對應 reservations 資料表
package tw.ispan.librarysystem.entity.reservation;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import tw.ispan.librarysystem.entity.books.BookEntity;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    // 預約狀態常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")
    private BookEntity book;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "reserve_time")
    private LocalDateTime reserveTime;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "status")
    private String status;  // 舊有字串型狀態欄位（如 PENDING、CANCELLED）

    @Column(name = "reserve_status")
    private Integer reserveStatus; // 0: 未完成, 1: 成功

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "pickup_method")
    private String pickupMethod;

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

    public Integer getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(Integer reserveStatus) {
        this.reserveStatus = reserveStatus;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupMethod() {
        return pickupMethod;
    }

    public void setPickupMethod(String pickupMethod) {
        this.pickupMethod = pickupMethod;
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
                ", reserveStatus=" + reserveStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", batchId='" + batchId + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", pickupMethod='" + pickupMethod + '\'' +
                '}';
    }
}
