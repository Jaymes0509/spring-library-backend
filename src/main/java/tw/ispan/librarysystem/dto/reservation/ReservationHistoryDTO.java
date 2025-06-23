package tw.ispan.librarysystem.dto.reservation;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationHistoryDTO {
    @JsonProperty("reservation_id")
    private Integer reservationId;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("book_id")
    private Integer bookId;
    
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    
    @JsonProperty("category_name")
    private String categoryName;
    
    @JsonProperty("reserve_time")
    private LocalDateTime reserveTime;
    
    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;
    
    private String status;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("pickup_location")
    private String pickupLocation;
    
    @JsonProperty("pickup_method")
    private String pickupMethod;

    // 無參數建構子
    public ReservationHistoryDTO() {}

    // 建構子
    public ReservationHistoryDTO(Integer reservationId, Integer userId, Integer bookId, 
                                String title, String author, String isbn, String publisher,
                                String categoryName, LocalDateTime reserveTime, 
                                LocalDateTime expiryDate, String status, 
                                LocalDateTime createdAt, LocalDateTime updatedAt, String batchId,
                                String pickupLocation, String pickupMethod) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.categoryName = categoryName;
        this.reserveTime = reserveTime;
        this.expiryDate = expiryDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.batchId = batchId;
        this.pickupLocation = pickupLocation;
        this.pickupMethod = pickupMethod;
    }

    // Getters and Setters
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDateTime getReserveTime() { return reserveTime; }
    public void setReserveTime(LocalDateTime reserveTime) { this.reserveTime = reserveTime; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getPickupMethod() { return pickupMethod; }
    public void setPickupMethod(String pickupMethod) { this.pickupMethod = pickupMethod; }
} 