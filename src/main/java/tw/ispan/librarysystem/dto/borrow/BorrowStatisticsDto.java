package tw.ispan.librarysystem.dto.borrow;

import lombok.Data;

@Data
public class BorrowStatisticsDto {
    private Long totalBorrows;
    private Long currentBorrows;
    private Long overdueBorrows;
    private Long returnedBorrows;
    private Long totalRenewals;
    private Double averageBorrowDuration;
    private String mostBorrowedCategory;
    private Long mostBorrowedBookId;
    private String mostBorrowedBookTitle;

    public BorrowStatisticsDto() {
        this.totalBorrows = 0L;
        this.currentBorrows = 0L;
        this.overdueBorrows = 0L;
        this.returnedBorrows = 0L;
        this.totalRenewals = 0L;
        this.averageBorrowDuration = 0.0;
    }

    public BorrowStatisticsDto(Long totalBorrows, Long currentBorrows, Long overdueBorrows, 
                              Long returnedBorrows, Long totalRenewals, Double averageBorrowDuration) {
        this.totalBorrows = totalBorrows;
        this.currentBorrows = currentBorrows;
        this.overdueBorrows = overdueBorrows;
        this.returnedBorrows = returnedBorrows;
        this.totalRenewals = totalRenewals;
        this.averageBorrowDuration = averageBorrowDuration;
    }
} 