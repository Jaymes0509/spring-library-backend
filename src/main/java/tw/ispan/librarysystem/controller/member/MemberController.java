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
        // 用 @Valid 驗證傳入的 DTO, 驗證通過才會進來
        try {
            System.out.println("✅ 後端收到的註冊資料如下：");
            System.out.println(dto); // 自動列印所有欄位（密碼也會顯示）

            // DTO 加的 @AssertTrue 方法（例如 isEducationValidByAge()）會在這裡自動觸發，驗證失敗就不會進入 memberService.register()，而是直接丟出錯誤。
            memberService.register(dto);
            return ResponseEntity.ok("註冊成功");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ 註冊失敗：" + e.getMessage()); // 傳回錯誤訊息給前端
        }
    }
}
