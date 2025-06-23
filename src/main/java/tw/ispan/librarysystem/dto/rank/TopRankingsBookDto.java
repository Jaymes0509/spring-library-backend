package tw.ispan.librarysystem.dto.rank;

public class TopRankingsBookDto {
    private Integer bookId;
    private String title;
    private String author;
    private String cover;       // 書封面 URL 或空字串
    private String categoryName; // 分類名稱
    private Double averageRating; // 平均評分（貝式平均）
    private Long statCount;      // 借閱/評論數量（用Long比較安全）

    // 無參數建構子（JPA 需要）
    public TopRankingsBookDto() {}

    // 7 個參數的建構子，對應 JPQL 查詢結果
    public TopRankingsBookDto(Integer bookId, String title, String author, String cover,
                              String categoryName, Double averageRating, Long statCount) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.categoryName = categoryName;
        this.averageRating = averageRating;
        this.statCount = statCount;
    }

    // Getter & Setter
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
}
