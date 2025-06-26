package tw.ispan.librarysystem.dto.rank;

public class TopRankingsBookDto {

    private Integer bookId;
    private String title;
    private String author;
    private String cover;         // 書封面 URL 或空字串
    private String categoryName; // 分類名稱
    private Double averageRating; // 平均評分（貝式平均）
    private Long statCount;       // 借閱/評論/預約數（依榜別而變）
    private String description;   // 書籍簡介 ✅ 新增欄位

    // ⚠️ 無參數建構子（JPA 或 Jackson 需要）
    public TopRankingsBookDto() {}

    // ✅ 全參數建構子（已補上 description）
    public TopRankingsBookDto(Integer bookId, String title, String author, String cover,
                              String categoryName, Double averageRating, Long statCount,
                              String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.categoryName = categoryName;
        this.averageRating = averageRating;
        this.statCount = statCount;
        this.description = description;
    }

    // --- Getter / Setter ---
    public Integer getBookId() {
        return bookId;
    }
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getStatCount() {
        return statCount;
    }
    public void setStatCount(Long statCount) {
        this.statCount = statCount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
