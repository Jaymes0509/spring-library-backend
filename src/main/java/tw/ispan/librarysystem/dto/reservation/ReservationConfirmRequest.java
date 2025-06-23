package tw.ispan.librarysystem.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationConfirmRequest {
    @JsonProperty("log_id")
    private Long logId;
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("book_id")
    private Integer bookId;
    
    private String note;

    // Getters and Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
} 