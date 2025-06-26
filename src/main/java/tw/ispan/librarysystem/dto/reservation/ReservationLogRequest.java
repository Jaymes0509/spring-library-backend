// 用來裝新增預約日誌時要送出的資料
package tw.ispan.librarysystem.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ReservationLogRequest {
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("book_id")
    private Integer bookId;
    
    private String action;
    
    @JsonProperty("reserve_time")
    private LocalDateTime reserveTime = LocalDateTime.now(); // 設定預設值
    
    private String note;

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public LocalDateTime getReserveTime() { return reserveTime; }
    public void setReserveTime(LocalDateTime reserveTime) { this.reserveTime = reserveTime; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
} 