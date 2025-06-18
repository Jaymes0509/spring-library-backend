package tw.ispan.librarysystem.dto.comment;

public class SearchBookDTO {
    private Integer bookId;
    private String title;
    private String author;
    private String categoryName;

    public SearchBookDTO(Integer bookId, String title, String author, String categoryName) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.categoryName = categoryName;
    }

    // Getter / Setter
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
