package tw.ispan.librarysystem.controller.rank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;
import tw.ispan.librarysystem.service.rank.TopRankingsService;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
public class TopRankingsController {

    private final TopRankingsService topRankingsService;

    @Autowired
    public TopRankingsController(TopRankingsService topRankingsService) {
        this.topRankingsService = topRankingsService;
    }

    // 📘 借閱排行榜，根據 categoryId, year 和 month 篩選
    @GetMapping("/borrows")
    public ResponseEntity<List<TopRankingsBookDto>> getBorrowRanking(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        List<TopRankingsBookDto> result = topRankingsService.getTopRankings(categoryId, year, month).getBorrowRanking();
        return ResponseEntity.ok(result);
    }

    // 📗 預約排行榜，根據 categoryId, year 和 month 篩選
    @GetMapping("/reservations")
    public ResponseEntity<List<TopRankingsBookDto>> getReservationRanking(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        List<TopRankingsBookDto> result = topRankingsService.getTopRankings(categoryId, year, month).getReservationRanking();
        return ResponseEntity.ok(result);
    }

    // 📙 評分排行榜（貝式平均），根據 categoryId, year 和 month 篩選
    @GetMapping("/bayesian")
    public ResponseEntity<List<TopRankingsBookDto>> getBayesianRatingRanking(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        List<TopRankingsBookDto> result = topRankingsService.getTopRankings(categoryId, year, month).getRatingRanking();
        return ResponseEntity.ok(result);
    }

    // 🏠 首頁用：三榜合併，根據 categoryId, year 和 month 篩選
    @GetMapping("/all")
    public ResponseEntity<TopRankingsDto> getAllRankings(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        TopRankingsDto result = topRankingsService.getTopRankings(categoryId, year, month);
        return ResponseEntity.ok(result);
    }
}
