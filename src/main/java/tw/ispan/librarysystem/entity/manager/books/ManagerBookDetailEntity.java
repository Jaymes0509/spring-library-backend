package tw.ispan.librarysystem.entity.manager.books;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookdetail")
public class ManagerBookDetailEntity {

    @Id
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "img_url")
    private String imgUrl;

    @OneToOne
    @MapsId
    @JoinColumn(name = "book_id")
    private ManagerBookEntity book;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ManagerBookEntity getBook() {
        return book;
    }

    public void setBook(ManagerBookEntity book) {
        this.book = book;
    }

}
