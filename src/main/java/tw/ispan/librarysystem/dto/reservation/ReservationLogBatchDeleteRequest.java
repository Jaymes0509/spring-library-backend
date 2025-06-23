package tw.ispan.librarysystem.dto.reservation;

import java.util.List;

/**
 * 批量刪除預約日誌的請求 DTO
 */
public class ReservationLogBatchDeleteRequest {
    
    private List<Long> logIds;

    public ReservationLogBatchDeleteRequest() {
    }

    public ReservationLogBatchDeleteRequest(List<Long> logIds) {
        this.logIds = logIds;
    }

    public List<Long> getLogIds() {
        return logIds;
    }

    public void setLogIds(List<Long> logIds) {
        this.logIds = logIds;
    }
} 