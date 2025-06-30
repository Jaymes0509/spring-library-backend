package tw.ispan.librarysystem.entity.member;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tw.ispan.librarysystem.entity.borrow.Borrow;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // 告訴 JPA 對應到 user_id 欄位
    private Long id;

    public Long getUserId() {
        return id;
    }
    public void setUserId(Long userId) {
        this.id = userId;
    }


    private String name;
    private String gender;
    private String idNumber;
    private LocalDate birthDate;
    private String nationality;
    private String education;
    private String occupation;
    private String addressCounty;
    private String addressTown;
    private String addressZip;
    private String addressDetail;
    private String email;
    private String phone;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member")
    @JsonIgnoreProperties({"member", "book"})
    private List<Borrow> borrows;

    // 手動添加 setter 方法
    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setAddressCounty(String addressCounty) {
        this.addressCounty = addressCounty;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 手動添加 getter 方法
    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public String getEducation() {
        return education;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddressCounty() {
        return addressCounty;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}

