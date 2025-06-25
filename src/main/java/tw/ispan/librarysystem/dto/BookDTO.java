package tw.ispan.librarysystem.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import tw.ispan.librarysystem.entity.books.BookEntity;

public class BookDTO {
    private Integer bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publishdate;
    private String version;
    private String type;
    private String language;
    private Integer cId;
    @JsonProperty("is_available")
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String classification;
    @JsonProperty("categorysystem")
    private String categorysystem;
    private String summary;
    private String imgUrl;

    

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

    // 建構函數
    public BookDTO() {}

    public BookDTO toDTO(BookEntity book) {
        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublishdate(book.getPublishdate());
        dto.setVersion(book.getVersion());
        dto.setType(book.getType());
        dto.setLanguage(book.getLanguage());
        dto.setClassification(book.getClassification());
        dto.setIsAvailable(book.getIsAvailable());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        
        
        if (book.getCategory() != null && book.getCategory().getCategorysystem() != null) {
            dto.setCategorysystem(book.getCategory().getCategorysystem().getCsName());
        }
    
        return dto;
    }

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

    public Integer getPublishdate() {
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

    public void setPublishdate(Integer publishdate) {
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

    public String getCategorysystem() {
        return categorysystem;
    }

    public void setCategorysystem(String categorysystem) {
        this.categorysystem = categorysystem;
    }
} 