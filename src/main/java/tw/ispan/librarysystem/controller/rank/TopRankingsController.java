package tw.ispan.librarysystem.controller.rank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;
import tw.ispan.librarysystem.service.rank.TopRankingsService;

@RestController
@RequestMapping("/api/rankings")
public class TopRankingsController {

    @Autowired
    private TopRankingsService topRankingsService;

    // 📘 首頁總覽榜單：前10名
    @GetMapping("/all")
    public TopRankingsDto getAllTopRankings(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return topRankingsService.getTopRankings(categoryId, year, month);
    }

    // 🔍 詳細搜尋榜單：支援分頁 + 條件搜尋
    @GetMapping("/detail")
    public Page<TopRankingsBookDto> getDetailedRanking(
            @RequestParam String type,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size);
        return topRankingsService.getDetailedRanking(type, categoryId, year, month, keyword, pageable);
    }
}
