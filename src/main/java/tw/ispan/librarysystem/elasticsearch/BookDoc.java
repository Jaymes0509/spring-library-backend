package tw.ispan.librarysystem.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDoc {
    @JsonProperty("book_id")
    private Integer bookId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("author")
    private String author;
    @JsonProperty("publisher")
    private String publisher;
    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("classification")
    private String classification;
    @JsonProperty("publishdate")
    private Integer publishdate;
    @JsonProperty("language")
    private String language;
    @JsonProperty("is_available")
    private Boolean isAvailable;
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;

    private String imgUrl;   // 額外欄位（來自 bookdetail）
    private String summary;  // 額外欄位（來自 bookdetail）

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
