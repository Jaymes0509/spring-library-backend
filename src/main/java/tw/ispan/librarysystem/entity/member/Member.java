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
}

