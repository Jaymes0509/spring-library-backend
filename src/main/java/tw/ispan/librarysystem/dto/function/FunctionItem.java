package tw.ispan.librarysystem.dto.function;

import lombok.Data;

@Data
public class FunctionItem {
    private String title;
    private String description;
    private String url;
    private String category; // 如果有分類可以加
}

