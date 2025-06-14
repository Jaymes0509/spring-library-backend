package tw.ispan.librarysystem.dto;

import java.time.LocalDateTime;

public class BookDTO {
    private Integer bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String publishdate;
    private String version;
    private String type;
    private String language;
    private Integer cId;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String classification;

    // 建構函數
    public BookDTO() {}

    // Getters
    public Integer getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishdate() {
        return publishdate;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getcId() {
        return cId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getClassification() {
        return classification;
    }

    // Setters
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
} 