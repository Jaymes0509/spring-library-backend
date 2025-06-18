package tw.ispan.librarysystem.entity.manager.books;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class ManagerCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Integer cId;

    @Column(name = "c_code")
    private String cCode;

    @Column(name = "c_name")
    private String cName;

    public Integer getcId() {
        return cId;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }

    public String getcCode() {
        return cCode;
    }

    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    // --------------------------------
    /** 一個 Category 可對應多筆 Book */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ManagerBookEntity> books;

    public List<ManagerBookEntity> getBooks() {
        return books;
    }

    public void setBooks(List<ManagerBookEntity> books) {
        this.books = books;
    }

    // --------------------------------
    /** 下面這段就是跟 CategorySystem 的關聯： */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cs_id")
    private ManagerCategorySystemEntity categorysystem;

    public ManagerCategorySystemEntity getCategorysystem() {
        return categorysystem;
    }

    public void setCategorysystem(ManagerCategorySystemEntity categorysystem) {
        this.categorysystem = categorysystem;
    }
}