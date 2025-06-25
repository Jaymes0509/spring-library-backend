package tw.ispan.librarysystem.service.rank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;
import tw.ispan.librarysystem.repository.rank.TopRankingsRepository;

import java.util.List;

@Service
public class TopRankingsServiceImpl implements TopRankingsService {

    @Autowired
    private TopRankingsRepository topRankingsRepository;

    // 📘 首頁總覽（預約 + 借閱 + 評分）
    @Override
    public TopRankingsDto getTopRankings(Integer categoryId, Integer year, Integer month) {
        Pageable top10 = Pageable.ofSize(10);

        List<TopRankingsBookDto> reservationList = topRankingsRepository.findTopRankingsByReservations(top10);
        List<TopRankingsBookDto> borrowList = topRankingsRepository.findTopRankingsByBorrows(top10);

        //可以使用全域變數
        double m = 5.0;      // 平均參考數
        double c = 3;      // 平均分數
        long minReviews = 10; // 最少評論門檻
        List<TopRankingsBookDto> ratingList = topRankingsRepository.findTopRankingsByRatings(m, c, minReviews, top10);

        return new TopRankingsDto(reservationList, borrowList, ratingList);
    }

    // 🔍 詳細查詢（根據 type 呼叫對應方法）
    @Override
    public Page<TopRankingsBookDto> getDetailedRanking(
            String type,
            Integer categoryId,
            Integer year,
            Integer month,
            String keyword,
            Pageable pageable
    ) {
        switch (type.toLowerCase()) {
            case "reservation":
                return topRankingsRepository.findTopRankingsByReservationCondition(
                        categoryId, year, month, keyword, pageable
                );

            case "borrow":
                return topRankingsRepository.findDetailedBorrows(
                        categoryId, year, month, keyword, pageable
                );

            case "rating":
                double m = 5.0;
                double c = 3;
                long minReviewCount = 10;
                return topRankingsRepository.findDetailedRatings(
                        categoryId, year, month, keyword, m, c, minReviewCount, pageable
                );

            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
