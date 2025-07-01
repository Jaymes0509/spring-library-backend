package tw.ispan.librarysystem.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import tw.ispan.librarysystem.document.BookDocument;
import tw.ispan.librarysystem.dto.search.SearchResultDto;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.service.function.FunctionSearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final FunctionSearchService functionSearchService;

    @Override
    public List<SearchResultDto> searchAll(String keyword) {
        List<SearchResultDto> results = new ArrayList<>();

        // 搜尋書籍（Elasticsearch）
        try {
            SearchResponse<BookDocument> response = elasticsearchClient.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("title", "author", "description")
                                            .query(keyword)
                                            .fuzziness("AUTO")
                                    )
                            ),
                    BookDocument.class
            );

            // 把 _id 設回 document 裡
            List<BookDocument> docs = response.hits().hits().stream().map(hit -> {
                BookDocument doc = hit.source();
                doc.setId(hit.id());
                return doc;
            }).collect(Collectors.toList());

            for (Hit<BookDocument> hit : response.hits().hits()) {
                BookDocument doc = hit.source();

                SearchResultDto dto = new SearchResultDto();
                dto.setType("書籍");
                dto.setTitle(doc.getTitle());
                dto.setUrl("/books?isbn=" + doc.getIsbn());
                dto.setIsbn(doc.getIsbn());
                dto.setImgUrl(doc.getImgUrl());

                results.add(dto);
            }


        } catch (IOException e) {
            e.printStackTrace(); // 可替換成 log
        }

        // 搜尋功能項
        results.addAll(functionSearchService.search(keyword));

        return results;
    }
}

