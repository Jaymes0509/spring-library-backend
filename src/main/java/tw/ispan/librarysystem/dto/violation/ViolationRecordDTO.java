package tw.ispan.librarysystem.dto.violation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * 違規記錄 DTO - 用於管理員查看所有違規記錄
 */
public class ViolationRecordDTO {
    
    @JsonProperty("violation_id")
    private Integer violationId;
    
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("user_email")
    private String userEmail;
    
    @JsonProperty("violation_type")
    private String violationType;
    
    @JsonProperty("violation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime violationDate;
    
    @JsonProperty("penalty_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime penaltyEndDate;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("days_remaining")
    private Long daysRemaining;

    // 建構函數
    public ViolationRecordDTO() {
    }

    public ViolationRecordDTO(Integer violationId, Integer userId, String violationType, 
                             LocalDateTime violationDate, LocalDateTime penaltyEndDate) {
        this.violationId = violationId;
        this.userId = userId;
        this.violationType = violationType;
        this.violationDate = violationDate;
        this.penaltyEndDate = penaltyEndDate;
        this.isActive = LocalDateTime.now().isBefore(penaltyEndDate);
        this.daysRemaining = calculateDaysRemaining(penaltyEndDate);
    }

    // 計算剩餘天數
    private Long calculateDaysRemaining(LocalDateTime penaltyEndDate) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(penaltyEndDate)) {
            return 0L;
        }
        return java.time.Duration.between(now, penaltyEndDate).toDays();
    }

    // Getters and Setters
    public Integer getViolationId() {
        return violationId;
    }

    public void setViolationId(Integer violationId) {
        this.violationId = violationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public LocalDateTime getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(LocalDateTime violationDate) {
        this.violationDate = violationDate;
    }

    public LocalDateTime getPenaltyEndDate() {
        return penaltyEndDate;
    }

    public void setPenaltyEndDate(LocalDateTime penaltyEndDate) {
        this.penaltyEndDate = penaltyEndDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
} 