package tw.ispan.librarysystem.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private String type;   // "功能" / "書籍"
    private String title;  // 顯示文字
    private String url;    // 導向連結

    private String isbn;
    private String imgUrl;
}

