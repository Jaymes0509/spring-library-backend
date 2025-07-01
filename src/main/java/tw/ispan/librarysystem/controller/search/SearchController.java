package tw.ispan.librarysystem.controller.search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.search.SearchResultDto;
import tw.ispan.librarysystem.service.search.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public List<
            SearchResultDto> search(@RequestParam("keyword") String keyword) {
        return searchService.searchAll(keyword);
    }
}

