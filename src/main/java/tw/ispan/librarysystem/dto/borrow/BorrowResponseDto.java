package tw.ispan.librarysystem.dto.borrow;

import lombok.Data;
import tw.ispan.librarysystem.entity.borrow.Borrow;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.entity.member.Member;

import java.time.LocalDateTime;

@Data
public class BorrowResponseDto {
    private Integer borrowId;
    private Integer userId;
    private Integer bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer renewCount;
    private String status;
    
    // 書籍資訊
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    
    // 會員資訊
    private String memberName;
    private String memberEmail;

    public BorrowResponseDto(Borrow borrow) {
        this.borrowId = borrow.getBorrowId();
        this.userId = borrow.getUserId();
        this.bookId = borrow.getBookId();
        this.borrowDate = borrow.getBorrowDate();
        this.dueDate = borrow.getDueDate();
        this.returnDate = borrow.getReturnDate();
        this.renewCount = borrow.getRenewCount();
        this.status = borrow.getStatus().name();
        
        // 設置書籍資訊
        if (borrow.getBook() != null) {
            BookEntity book = borrow.getBook();
            this.bookTitle = book.getTitle();
            this.bookAuthor = book.getAuthor();
            this.bookIsbn = book.getIsbn();
        }
        
        // 設置會員資訊
        if (borrow.getMember() != null) {
            Member member = borrow.getMember();
            this.memberName = member.getName();
            this.memberEmail = member.getEmail();
        }
    }
} 