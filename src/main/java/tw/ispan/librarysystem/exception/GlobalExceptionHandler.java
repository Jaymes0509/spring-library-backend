package tw.ispan.librarysystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 作用是統一處理整個專案內所有 @Valid 或 @ExceptionHandler 拋出的錯誤。
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // 處理欄位錯誤（如 @NotBlank）
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // 處理全域錯誤（如 @AssertTrue）
        ex.getBindingResult().getGlobalErrors().forEach(error ->
                errors.put("error", error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }
}

// 當你前端送出錯誤資料，例如：
//education 與年齡不符（@AssertTrue）
//birthDate 是未來日期（@PastOrPresent）
//email 格式不對（@Email）
// ↓
// 後端回傳{
//  "education": "您選擇的學歷與年齡不符",
//  "email": "必須為合法的電子郵件格式"
//}