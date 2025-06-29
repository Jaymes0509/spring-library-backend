package tw.ispan.librarysystem.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDocument {
    private String id;
    private String title;
    private String author;
    private String description;
    private Long book_id; //一定要加這行，才能對應同步進 ES 的欄位
    private String isbn;
    private String publisher;
    private String publishdate;
    private String imgUrl;

}

