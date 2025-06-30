package tw.ispan.librarysystem.dto.member;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // 教育驗證
//    @AssertTrue(message = "您選擇的學歷與年齡不符")
//    public boolean isEducationValidByAge() {
//        if (birthDate == null || education == null) return true;
//        int age = Period.between(birthDate, LocalDate.now()).getYears();
//        Set<String> validEducations = new HashSet<>();
//
//        if (age < 6) {
//            validEducations.add("學齡前");
//        } else if (age < 12) {
//            validEducations.addAll(List.of("學齡前", "國小"));
//        } else if (age < 15) {
//            validEducations.addAll(List.of("學齡前", "國小", "國中 (初中)"));
//        } else if (age < 18) {
//            validEducations.addAll(List.of("學齡前", "國小", "國中 (初中)", "高中 (高職)", "專科"));
//        } else if (age < 21) {
//            validEducations.addAll(List.of("學齡前", "國小", "國中 (初中)", "高中 (高職)", "專科", "大學"));
//        } else {
//            return true;
//        }
//
//        return validEducations.contains(education);
//    }

    // 職業驗證（可選）
//    @AssertTrue(message = "15歲以下僅能選擇學生類職業")
//    public boolean isOccupationValidByAge() {
//        if (birthDate == null || occupation == null) return true;
//        int age = Period.between(birthDate, LocalDate.now()).getYears();
//        if (age <= 15) {
//            return occupation.startsWith("本國學生;僑生") || occupation.startsWith("外籍學生");
//        }
//        return true;
//    }
}

