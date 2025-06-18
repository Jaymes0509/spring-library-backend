package tw.ispan.librarysystem.service.comment;

import org.springframework.data.domain.Page;
import tw.ispan.librarysystem.dto.comment.SearchBookDTO;

public interface SearchBookService {
    Page<SearchBookDTO> searchBooks(String keyword, String category, int page, int pageSize);
}
