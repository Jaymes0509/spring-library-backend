package tw.ispan.librarysystem.controller.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.comment.SearchBookDTO;
import tw.ispan.librarysystem.service.comment.SearchBookService;

@RestController
@RequestMapping("/api/search-books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SearchBookController {

    @Autowired
    private SearchBookService searchBookService;

    @GetMapping
    public Page<SearchBookDTO> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return searchBookService.searchBooks(keyword, category, page, pageSize);
    }
}
