package tw.ispan.librarysystem.entity.manager.books;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorysystem")
public class ManagerCategorySystemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cs_id")
    private Integer csId;

    @Column(name = "cs_code")
    private String csCode;

    @Column(name = "cs_name")
    private String csName;

    @Column(name = "cs_description")
    private String csDescription;

    public Integer getCsId() {
        return csId;
    }

    public void setCsId(Integer csId) {
        this.csId = csId;
    }

    public String getCsCode() {
        return csCode;
    }

    public void setCsCode(String csCode) {
        this.csCode = csCode;
    }

    public String getCsName() {
        return csName;
    }

    public void setCsName(String csName) {
        this.csName = csName;
    }

    public String getCsDescription() {
        return csDescription;
    }

    public void setCsDescription(String csDescription) {
        this.csDescription = csDescription;
    }

    // --------------------------------
    @OneToMany(mappedBy = "categorysystem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ManagerCategoryEntity> category;

    public List<ManagerCategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(List<ManagerCategoryEntity> category) {
        this.category = category;
    }
}
