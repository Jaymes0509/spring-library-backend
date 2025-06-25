package tw.ispan.librarysystem.elasticsearch;

/**
 * 專屬於 Elasticsearch 搜尋 API 回傳用的 Response
 * 包含 ES 主資料欄位與 imgUrl、summary（來自資料庫）
 */
public class BookSearchResponse {
    private Integer bookId;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String classification;
    private Integer publishdate;
    private String language;
    private Boolean isAvailable;
    private String type;
    private String version;
    private String imgUrl;   // 來自資料庫
    private String summary;  // 來自資料庫

    // getter/setter
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public Integer getPublishdate() { return publishdate; }
    public void setPublishdate(Integer publishdate) { this.publishdate = publishdate; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
} 