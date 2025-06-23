package tw.ispan.librarysystem.service.rank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;
import tw.ispan.librarysystem.repository.rank.TopRankingsRepository;

import java.util.List;

@Service
public class TopRankingsService {

    private final TopRankingsRepository topRankingsRepository;

    @Autowired
    public TopRankingsService(TopRankingsRepository topRankingsRepository) {
        this.topRankingsRepository = topRankingsRepository;
    }

    public TopRankingsDto getTopRankings(Integer categoryId, Integer year, Integer month) {
        Pageable top10 = PageRequest.of(0, 10);  // 每頁10筆資料

        // 📌 預約排行榜
        List<TopRankingsBookDto> reservationRanking =
                topRankingsRepository.findTopRankingsByReservations(top10);

        // 📌 借閱排行榜
        List<TopRankingsBookDto> borrowRanking =
                topRankingsRepository.findTopRankingsByBorrows(categoryId, year, month, top10);

        // 📌 評分排行榜（使用貝式平均）
        double m = 5.0;              // 評論數門檻參考值
        double c = 3.0;              // 全站平均評分（可從資料庫取值）
        long minReviewCount = 10;    // 最少評論數要求

        List<TopRankingsBookDto> ratingRanking =
                topRankingsRepository.findTopRankingsByRatings(m, c, minReviewCount, top10);

        return new TopRankingsDto(reservationRanking, borrowRanking, ratingRanking);
    }
}
