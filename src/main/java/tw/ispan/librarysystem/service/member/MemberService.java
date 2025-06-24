package tw.ispan.librarysystem.service.member;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.member.MemberRegisterDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepo;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(MemberRegisterDto dto) {
        if (memberRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("該 Email 已註冊");// 檢查 email 是否重複
        }

        if (memberRepo.existsByIdNumber(dto.getIdNumber())) {
            throw new RuntimeException("該身分證已申請過"); // 檢查身分證是否重複
        }

        Member member = new Member();
        BeanUtils.copyProperties(dto, member);

        // 密碼加密
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        memberRepo.save(member);
    }

    /**
     * 驗證登入
     * 
     * @param email    電子郵件
     * @param password 密碼
     * @return true 如果驗證成功，false 如果失敗
     */
    public boolean validateLogin(String email, String password) {
        Optional<Member> memberOpt = memberRepo.findByEmail(email);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            // 使用 BCrypt 驗證密碼
            return passwordEncoder.matches(password, member.getPassword());
        }
        return false;
    }

    /**
     * 根據 email 取得會員資訊
     * 
     * @param email 電子郵件
     * @return 會員實體，如果不存在則返回 null
     */
    public Member getMemberByEmail(String email) {
        return memberRepo.findByEmail(email).orElse(null);
    }
}
