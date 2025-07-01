package tw.ispan.librarysystem.dto.manager.accounts;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ManagerMemberDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}