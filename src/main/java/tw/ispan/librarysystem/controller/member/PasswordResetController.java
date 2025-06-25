package tw.ispan.librarysystem.controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.service.member.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        passwordResetService.resetPasswordAndSendEmail(email);

        return ResponseEntity
                .ok("密碼重置信件已寄出，請至信箱查看");
    }
}
