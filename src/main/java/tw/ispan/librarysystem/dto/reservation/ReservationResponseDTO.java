package tw.ispan.librarysystem.dto.reservation;

import java.util.List;

public class ReservationResponseDTO {
    private boolean success;
    private List<Result> results;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public List<Result> getResults() {
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }

    public static class Result {
        private Integer bookId;
        private Integer reservationId;
        private String status; // success/fail
        private String reason;

        public Integer getBookId() {
            return bookId;
        }
        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }
        public Integer getReservationId() {
            return reservationId;
        }
        public void setReservationId(Integer reservationId) {
            this.reservationId = reservationId;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
} 