package tw.ispan.librarysystem.controller.member;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.member.MemberRegisterDto;
import tw.ispan.librarysystem.service.member.MemberService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") //
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid MemberRegisterDto dto) {
        try {
            System.out.println("✅ 後端收到的註冊資料如下：");
            System.out.println(dto); // 自動列印所有欄位（密碼也會顯示）

            memberService.register(dto);
            return ResponseEntity.ok("註冊成功");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ 註冊失敗：" + e.getMessage()); // 傳回錯誤訊息給前端
        }
    }
}
