package tw.ispan.librarysystem.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.BookDTO;
import tw.ispan.librarysystem.dto.PageResponseDTO;
import tw.ispan.librarysystem.entity.books.BookDetailEntity;
import tw.ispan.librarysystem.repository.books.BookDetailRepository;
import tw.ispan.librarysystem.elasticsearch.EsQueryCondition;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;

@Service
public class BookElasticsearchService {
    
    private static final Logger log = LoggerFactory.getLogger(BookElasticsearchService.class);
    private final ElasticsearchClient client;
    private final BookDetailRepository bookDetailRepository;
    private static final String BOOKS_INDEX = "books";

    @Autowired
    public BookElasticsearchService(ElasticsearchClient client, BookDetailRepository bookDetailRepository) {
        this.client = client;
        this.bookDetailRepository = bookDetailRepository;
    }

    /**
     * 檢查索引是否存在
     */
    private boolean indexExists(String indexName) {
        try {
            return client.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value();
        } catch (IOException e) {
            log.error("檢查索引 {} 是否存在時發生錯誤", indexName, e);
            return false;
        }
    }

    public PageResponseDTO<BookSearchResponse> searchBooks(String field, String keyword,
                                                int page, int size,
                                                String sortField, String sortDir) throws IOException {
        
        // 檢查索引是否存在
        if (!indexExists(BOOKS_INDEX)) {
            log.warn("索引 {} 不存在，返回空結果", BOOKS_INDEX);
            return new PageResponseDTO<>(List.of(), page, size, 0, 0, true, page == 0);
        }

        // 排序欄位若是 text，則使用 keyword 子欄位
        final String sortFieldForEs;
        if (List.of("title", "author", "publisher").contains(sortField)) {
            sortFieldForEs = sortField + ".keyword";
        } else {
            sortFieldForEs = sortField;
        }

        int from = page * size;
        SearchResponse<BookDoc> response;
        String[] termFields = {"isbn", "language", "classification", "publishdate"};
        
        try {
            co.elastic.clients.elasticsearch._types.query_dsl.Query query;
            String queryType = "";
            if ("fulltext".equals(field)) {
                if (keyword.matches("\\d{13}")) {
                    queryType = "term (isbn)";
                    query = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.term(t -> t.field("isbn").value(keyword)));
                } else if (keyword.contains("*") || keyword.contains("?")) {
                    queryType = "multi_match (wildcard, best_fields)";
                    query = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.multiMatch(m -> m
                        .fields("title", "author", "publisher", "isbn")
                        .query(keyword)
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                    ));
                } else if (keyword.contains(" ")) {
                    queryType = "multi_match (phrase)";
                    query = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.multiMatch(m -> m
                        .fields("title", "author", "publisher", "isbn")
                        .query(keyword)
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.Phrase)
                    ));
                } else {
                    queryType = "multi_match (best_fields)";
                    query = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.multiMatch(m -> m
                        .fields("title", "author", "publisher", "isbn")
                        .query(keyword)
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                    ));
                }
            } else {
                // 保留原本單一欄位查詢邏輯（如有需要）
                query = buildQueryByInput(field, keyword);
                queryType = "custom field query";
            }
            log.info("查詢型態: {}, field={}, keyword={}", queryType, field, keyword);
            response = client.search(s -> s
                .index(BOOKS_INDEX)
                .from(from)
                .size(size)
                .sort(so -> so
                    .field(f -> f
                        .field(sortFieldForEs)
                        .order("asc".equalsIgnoreCase(sortDir) ? SortOrder.Asc : SortOrder.Desc)
                    )
                )
                .query(query),
                BookDoc.class
            );

            List<BookDoc> docs = response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            List<Integer> bookIds = docs.stream().map(BookDoc::getBookId).filter(Objects::nonNull).collect(Collectors.toList());
            List<BookDetailEntity> details = bookIds.isEmpty() ? List.of() : bookDetailRepository.findAllById(bookIds);
            Map<Integer, BookDetailEntity> detailMap = details.stream().collect(Collectors.toMap(BookDetailEntity::getBookId, d -> d));
            List<BookSearchResponse> dtoList = docs.stream().map(doc -> {
                BookSearchResponse dto = new BookSearchResponse();
                dto.setBookId(doc.getBookId());
                dto.setTitle(doc.getTitle());
                dto.setAuthor(doc.getAuthor());
                dto.setPublisher(doc.getPublisher());
                dto.setIsbn(doc.getIsbn());
                dto.setClassification(doc.getClassification());
                dto.setPublishdate(doc.getPublishdate());
                dto.setLanguage(doc.getLanguage());
                dto.setIsAvailable(doc.getIsAvailable());
                dto.setType(doc.getType());
                dto.setVersion(doc.getVersion());
                BookDetailEntity detail = detailMap.get(doc.getBookId());
                if (detail != null) {
                    dto.setImgUrl(detail.getImgUrl());
                    dto.setSummary(detail.getSummary());
                }
                return dto;
            }).collect(Collectors.toList());

            long total = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) total / size);

            return new PageResponseDTO<>(dtoList, page, size, total, totalPages,
                page + 1 == totalPages, page == 0);
                
        } catch (Exception e) {
            log.error("搜尋書籍時發生錯誤: field={}, keyword={}", field, keyword, e);
            throw new IOException("搜尋失敗: " + e.getMessage(), e);
        }
    }

    public void indexBook(BookDoc doc) throws IOException {
        if (doc == null || doc.getBookId() == null) {
            throw new IllegalArgumentException("書籍文件或書籍ID不能為空");
        }

        try {
            client.index(IndexRequest.of(i -> i
                .index(BOOKS_INDEX)
                .id(doc.getBookId().toString())
                .document(doc)
            ));
            log.info("成功索引書籍: ID={}, 標題={}", doc.getBookId(), doc.getTitle());
        } catch (Exception e) {
            log.error("索引書籍時發生錯誤: ID={}", doc.getBookId(), e);
            throw new IOException("索引失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 批量索引書籍
     */
    public void indexBooks(List<BookDoc> docs) throws IOException {
        if (docs == null || docs.isEmpty()) {
            log.warn("沒有書籍需要索引");
            return;
        }

        try {
            for (BookDoc doc : docs) {
                indexBook(doc);
            }
            log.info("成功批量索引 {} 本書籍", docs.size());
        } catch (Exception e) {
            log.error("批量索引書籍時發生錯誤", e);
            throw new IOException("批量索引失敗: " + e.getMessage(), e);
        }
    }

    // 根據使用者輸入自動判斷查詢類型
    private co.elastic.clients.elasticsearch._types.query_dsl.Query buildQueryByInput(String field, String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("查詢關鍵字不能為空");
        }
        if (field.equals("isbn") && input.matches("\\d{13}")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.term(t -> t.field(field).value(input)));
        }
        if (input.contains("*") || input.contains("?")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.wildcard(w -> w.field(field).value(input)));
        }
        if (input.contains(" ")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.matchPhrase(mp -> mp.field(field).query(input)));
        }
        return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.match(m -> m.field(field).query(input)));
    }
}
