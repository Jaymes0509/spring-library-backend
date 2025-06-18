package tw.ispan.librarysystem.service.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.comment.SearchBookDTO;
import tw.ispan.librarysystem.repository.comment.SearchBookRepository;

@Service
public class SearchBookServiceImpl implements SearchBookService {

    @Autowired
    private SearchBookRepository searchBookRepository;

    @Override
    public Page<SearchBookDTO> searchBooks(String keyword, String category, int page, int pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        if ((keyword == null || keyword.isEmpty()) && (category == null || category.isEmpty())) {
            return searchBookRepository.searchBooksByCategoryAndKeyword(null, "", pageable);
        } else {
            return searchBookRepository.searchBooksByCategoryAndKeyword(category, keyword == null ? "" : keyword, pageable);
        }
    }
}
