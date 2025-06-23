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
public class TopRankingsServiceImpl implements TopRankingsService {

    private final TopRankingsRepository topRankingsRepository;

    @Autowired
    public TopRankingsServiceImpl(TopRankingsRepository topRankingsRepository) {
        this.topRankingsRepository = topRankingsRepository;
    }

    // 📘 首頁總覽榜單
    @Override
    public TopRankingsDto getTopRankings(Integer categoryId, Integer year, Integer month) {
        Pageable top10 = PageRequest.of(0, 10);

        List<TopRankingsBookDto> reservationRanking =
                topRankingsRepository.findTopRankingsByReservations(top10);

        List<TopRankingsBookDto> borrowRanking =
                topRankingsRepository.findTopRankingsByBorrows(categoryId, year, month, top10);

        double m = 5.0;
        double c = 3.0;
        long minReviewCount = 10;

        List<TopRankingsBookDto> ratingRanking =
                topRankingsRepository.findTopRankingsByRatings(m, c, minReviewCount, top10);

        return new TopRankingsDto(reservationRanking, borrowRanking, ratingRanking);
    }

    // 🔍 詳細條件搜尋榜單
    @Override
    public List<TopRankingsBookDto> getDetailedRanking(
            String type,
            Integer categoryId,
            Integer year,
            Integer month,
            String keyword
    ) {
        Pageable top10 = PageRequest.of(0, 10);

        return switch (type) {
            case "reservation" -> topRankingsRepository.findTopRankingsByReservationCondition(categoryId, year, month, keyword, top10);
            case "borrow" -> topRankingsRepository.findDetailedBorrows(categoryId, year, month, keyword, top10);
            case "rating" -> {
                double m = 5.0;
                double c = 3.0;
                long minReviewCount = 10;
                yield topRankingsRepository.findDetailedRatings(categoryId, year, month, keyword, m, c, minReviewCount, top10);
            }
            default -> throw new IllegalArgumentException("Invalid ranking type: " + type);
        };
    }
}
