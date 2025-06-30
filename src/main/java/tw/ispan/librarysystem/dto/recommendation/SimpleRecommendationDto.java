package tw.ispan.librarysystem.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SimpleRecommendationDto {
    private String title;
    private String isbn;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}

// 這個DTO是後端回傳推薦清單資料給前端顯示用
// 用於展示「使用者推薦過哪些書」
// 讓前端能顯示書名、推薦理由、審核狀態、送出時間

//不應含內部 DB id 或 Member 等資訊（避免洩露）

