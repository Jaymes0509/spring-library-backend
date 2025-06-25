package tw.ispan.librarysystem.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRegisterDto {
    @NotBlank private String name;
    @NotBlank private String gender;
    @NotBlank private String idNumber;

    @NotNull(message = "請填寫出生日期")
    @PastOrPresent(message = "出生日期不能是未來")
    private LocalDate birthDate;

    @NotBlank private String nationality;
    @NotBlank private String education;
    @NotBlank private String occupation;
    @NotBlank private String addressCounty;
    @NotBlank private String addressTown;
    @NotBlank private String addressZip;
    @NotBlank private String addressDetail;

    @Email
    @NotBlank private String email;

    @NotBlank private String phone;

    @NotBlank
    private String password; // 加密處理
}

