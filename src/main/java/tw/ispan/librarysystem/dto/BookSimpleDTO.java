package tw.ispan.librarysystem.dto;

public class BookSimpleDTO {
    private Integer bookId;
    private String isbn;
    private String title;
    private String author;
    private String imgUrl;
    private String publisher;
    private String publishdate;

    public BookSimpleDTO(Integer bookId, String isbn, String title, String author, String imgUrl, String publisher, String publishdate) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.imgUrl = imgUrl;
        this.publisher = publisher;
        this.publishdate = publishdate;
    }

    public Integer getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getImgUrl() { return imgUrl; }
    public String getPublisher() { return publisher; }
    public String getPublishdate() { return publishdate; }

    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPublishdate(String publishdate) { this.publishdate = publishdate; }
} 