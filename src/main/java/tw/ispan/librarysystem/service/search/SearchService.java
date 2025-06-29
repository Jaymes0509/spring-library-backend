package tw.ispan.librarysystem.service.search;

import tw.ispan.librarysystem.dto.search.SearchResultDto;

import java.util.List;

public interface SearchService {
    List<SearchResultDto> searchAll(String keyword);
}

