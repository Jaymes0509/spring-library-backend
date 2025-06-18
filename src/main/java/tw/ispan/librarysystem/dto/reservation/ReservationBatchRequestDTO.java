package tw.ispan.librarysystem.dto.reservation;

import java.util.List;

public class ReservationBatchRequestDTO {
    private Integer userId;
    private List<BookReserveItem> books;

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public List<BookReserveItem> getBooks() {
        return books;
    }
    public void setBooks(List<BookReserveItem> books) {
        this.books = books;
    }

    public static class BookReserveItem {
        private Integer bookId;
        private String reserveTime; // ISO 格式字串

        public Integer getBookId() {
            return bookId;
        }
        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }
        public String getReserveTime() {
            return reserveTime;
        }
        public void setReserveTime(String reserveTime) {
            this.reserveTime = reserveTime;
        }
    }
} 