package tw.ispan.librarysystem.controller.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException;

import tw.ispan.librarysystem.dto.member.MemberLoginDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.service.member.MemberService;
import tw.ispan.librarysystem.util.JwtTool;
import tw.ispan.librarysystem.security.CheckJwt;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberLoginController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberLoginDto loginDto) {
        try {
            // 驗證帳號密碼
            boolean isValid = memberService.validateLogin(loginDto.getEmail(), loginDto.getPassword());

            if (!isValid) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "帳號或密碼錯誤");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // 取得會員資訊
            Member member = memberService.getMemberByEmail(loginDto.getEmail());

            // 產生 JWT token
            String token = JwtTool.createToken(loginDto.getEmail());

            // 回傳成功結果
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登入成功");
            response.put("token", token);
            response.put("user", Map.of(
                    "id", member.getId(),
                    "name", member.getName(),
                    "email", member.getEmail(),
                    "jwt", token,
                    "jwt_exp", JwtTool.getExpirationTime(token)));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "登入失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/validate")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // 移除 "Bearer " 前綴
            String token = authHeader.replace("Bearer ", "");

            // 驗證 token
            boolean isValid = JwtTool.validateToken(token);

            if (isValid) {
                String email = JwtTool.parseToken(token);
                Member member = memberService.getMemberByEmail(email);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("valid", true);
                response.put("user", Map.of(
                        "id", member.getId(),
                        "name", member.getName(),
                        "email", member.getEmail(),
                        "jwt", token,
                        "jwt_exp", JwtTool.getExpirationTime(token)));

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("valid", false);
                response.put("message", "Token 無效");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("valid", false);
            response.put("message", "Token 驗證失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/test")
    @CheckJwt
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }
}
