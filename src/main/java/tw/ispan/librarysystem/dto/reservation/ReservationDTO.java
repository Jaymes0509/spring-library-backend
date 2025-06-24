// 用來裝單一筆預約的詳細資料
package tw.ispan.librarysystem.dto.reservation;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    @JsonProperty("reservation_id")
    private Integer reservationId;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("reserve_time")
    private LocalDateTime reserveTime;
    
    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;
    
    private String status;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    // Book information
    @JsonProperty("book_id")
    private Integer bookId;
    
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String classification;
    
    @JsonProperty("category_name")
    private String categoryName;

    // 新增取書相關欄位
    @JsonProperty("pickup_location")
    private String pickupLocation;
    
    @JsonProperty("pickup_time")
    private LocalDateTime pickupTime;

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("pickup_method")
    private String pickupMethod;

    // ✅ 新增這段 constructor 給 JPQL 使用
    public ReservationDTO(Integer reservationId, String status, String title, LocalDateTime reserveTime) {
        this.reservationId = reservationId;
        this.status = status;
        this.title = title;
        this.reserveTime = reserveTime;
    }
}
