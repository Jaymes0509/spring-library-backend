package tw.ispan.librarysystem.elasticsearch;

public class BookSearchRequest {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String classification;
    private Integer yearFrom;
    private Integer yearTo;
    private String language;
    private Integer page = 0;
    private Integer size = 10;
    private String sortField = "title";
    private String sortDir = "asc";
    // getter/setter
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
    public Integer getYearFrom() { return yearFrom; }
    public void setYearFrom(Integer yearFrom) { this.yearFrom = yearFrom; }
    public Integer getYearTo() { return yearTo; }
    public void setYearTo(Integer yearTo) { this.yearTo = yearTo; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
} 