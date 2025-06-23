package tw.ispan.librarysystem.service.rank;

import tw.ispan.librarysystem.dto.rank.TopRankingsBookDto;
import tw.ispan.librarysystem.dto.rank.TopRankingsDto;

import java.util.List;

public interface TopRankingsService {

    // 📘 首頁總覽用（原 /all）
    TopRankingsDto getTopRankings(Integer categoryId, Integer year, Integer month);

    // 🔍 詳細搜尋用（/detail）
    List<TopRankingsBookDto> getDetailedRanking(
            String type,
            Integer categoryId,
            Integer year,
            Integer month,
            String keyword
    );
}
