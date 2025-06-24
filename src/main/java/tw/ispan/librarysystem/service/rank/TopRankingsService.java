package tw.ispan.librarysystem.service.rank;

import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopRankingsService {

    // 📘 首頁總覽用（原 /all）
    TopRankingsDto getTopRankings(Integer categoryId, Integer year, Integer month);

    // 🔍 詳細搜尋用（/detail）
    Page<TopRankingsBookDto> getDetailedRanking(
            String type,
            Integer categoryId,
            Integer year,
            Integer month,
            String keyword,
            Pageable pageable
    );

}
