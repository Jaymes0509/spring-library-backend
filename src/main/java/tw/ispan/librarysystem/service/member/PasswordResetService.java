package tw.ispan.librarysystem.service.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PasswordResetService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    public void resetPasswordAndSendEmail(String email) {
        memberRepository.findByEmail(email).ifPresent(member -> {
            // 產生新的隨機密碼
            String newPassword = generateRandomPassword();

            // 將新密碼加密後存入資料庫
            member.setPassword(passwordEncoder.encode(newPassword));
            // @UpdateTimestamp 會自動更新 updatedAt 欄位
            memberRepository.save(member);

            // 寄送包含新密碼的電子郵件
            sendNewPasswordEmail(member, newPassword);
        });
    }

    private String generateRandomPassword() {
        return IntStream.range(0, PASSWORD_LENGTH)
                .map(i -> random.nextInt(PASSWORD_ALLOW_BASE.length()))
                .mapToObj(randomIndex -> String.valueOf(PASSWORD_ALLOW_BASE.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

    private void sendNewPasswordEmail(Member member, String newPassword) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(member.getEmail());
        emailMessage.setSubject("您的新密碼 / Your New Password");
        emailMessage.setText(
                "您好 " + member.getName() + ",\n\n您的密碼已被重設。\n\n您的新密碼是: " + newPassword + "\n\n登入後請記得變更您的密碼。\n\n謝謝您。");
        mailSender.send(emailMessage);
    }
}
