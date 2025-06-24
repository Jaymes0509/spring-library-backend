package tw.ispan.librarysystem.service.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.repository.member.MemberRepository;
import tw.ispan.librarysystem.entity.member.Member;

import java.util.Optional;

@Service
public class MemberLoginService {
    @Autowired
    private MemberRepository memberRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean validate(String email, String password) {
        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            // 使用 BCrypt 驗證密碼
            return passwordEncoder.matches(password, member.getPassword());
        }
        return false;
    }
}