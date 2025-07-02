package tw.ispan.librarysystem.dto.reservation;

import java.util.List;

/**
 * 批量刪除預約日誌請求 DTO，封裝批量刪除日誌的資料
 */
public class ReservationLogBatchDeleteRequest {
    
    private List<Integer> logIds;

    public ReservationLogBatchDeleteRequest() {
    }

    public ReservationLogBatchDeleteRequest(List<Integer> logIds) {
        this.logIds = logIds;
    }

    public List<Integer> getLogIds() {
        return logIds;
    }

    public void setLogIds(List<Integer> logIds) {
        this.logIds = logIds;
    }
} 