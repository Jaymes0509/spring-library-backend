package tw.ispan.librarysystem.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.PageResponseDTO;
import tw.ispan.librarysystem.entity.books.BookDetailEntity;
import tw.ispan.librarysystem.repository.books.BookDetailRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        final String sortFieldForEs = List.of("title", "author", "publisher").contains(sortField)
            ? sortField + ".keyword"
            : sortField;

        int from = page * size;
        SearchResponse<BookDoc> response;
        
        try {
            co.elastic.clients.elasticsearch._types.query_dsl.Query query;
            String queryType = "";

            if ("fulltext".equals(field)) {
                // 1. 優先處理 ISBN 搜尋
                if (keyword.matches("\\d{10,13}")) {
                    queryType = "term (isbn)";
                    query = Query.of(q -> q.term(t -> t.field("isbn").value(keyword)));
                } 
                // 2. 處理萬用字元搜尋 (使用真正的 wildcard 查詢)
                else if (keyword.contains("*") || keyword.contains("?")) {
                    queryType = "wildcard (multi-field with boost)";
                    query = Query.of(q -> q.bool(b -> b
                        .should(s -> s.wildcard(w -> w.field("title").value(keyword).boost(3.0f)))
                        .should(s -> s.wildcard(w -> w.field("author").value(keyword).boost(2.0f)))
                        .should(s -> s.wildcard(w -> w.field("publisher").value(keyword).boost(1.0f)))
                        .should(s -> s.wildcard(w -> w.field("isbn").value(keyword).boost(1.0f)))
                    ));
                } 
                // 3. 處理多詞彙搜尋（片語搜尋）- 修正判斷邏輯
                else if (isMultiWordQuery(keyword)) {
                    queryType = "multi_match (phrase with boost)";
                    query = Query.of(q -> q.multiMatch(m -> m
                        .fields("title^3", "author^2", "publisher", "isbn")
                        .query(keyword)
                        .type(TextQueryType.Phrase)
                        .minimumShouldMatch("75%")
                        .slop(1)
                    ));
                } 
                // 4. 一般單詞搜尋
                else {
                    queryType = "multi_match (best_fields with boost)";
                    query = Query.of(q -> q.multiMatch(m -> m
                        .fields("title^3", "author^2", "publisher", "isbn")
                        .query(keyword)
                        .type(TextQueryType.BestFields)
                        .minimumShouldMatch("75%")
                        .fuzziness("AUTO")
                    ));
                }
            } else {
                queryType = "custom field query";
                query = buildQueryByInput(field, keyword);
            }

            log.info("\n查詢型態: {}, field={}, keyword={}", queryType, field, keyword);

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

            List<Integer> bookIds = docs.stream().map(BookDoc::getBookId).filter(Objects::nonNull).toList();
            Map<Integer, BookDetailEntity> detailMap = bookIds.isEmpty()
                ? Map.of()
                : bookDetailRepository.findAllById(bookIds).stream()
                    .collect(Collectors.toMap(BookDetailEntity::getBookId, d -> d));

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
            }).toList();

            long total = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) total / size);

            return new PageResponseDTO<>(
                dtoList, page, size, total, totalPages,
                page + 1 == totalPages, page == 0
            );

        } catch (Exception e) {
            log.error("搜尋書籍時發生錯誤: field={}, keyword={}", field, keyword, e);
            
            // 改善錯誤處理
            if (e instanceof IllegalArgumentException) {
                throw new IOException("搜尋參數錯誤: " + e.getMessage(), e);
            } else if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException("搜尋失敗: " + e.getMessage(), e);
            }
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

    /**
     * 判斷是否為多詞彙查詢
     */
    private boolean isMultiWordQuery(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        
        String trimmedKeyword = keyword.trim();
        String[] words = trimmedKeyword.split("\\s+");
        
        // 至少包含2個詞彙，且不是單一詞彙
        return words.length >= 2 && !trimmedKeyword.equals(words[0]);
    }

    /**
     * 根據使用者輸入自動判斷查詢類型 - 修正萬用字元處理
     */
    private co.elastic.clients.elasticsearch._types.query_dsl.Query buildQueryByInput(String field, String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("查詢關鍵字不能為空");
        }
        
        // 修正: 萬用字元查詢應該使用 wildcard
        if (input.contains("*") || input.contains("?")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.wildcard(w -> w
                .field(field)
                .value(input)
            ));
        }
        
        if (field.equals("isbn") && input.matches("\\d{13}")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.term(t -> t.field(field).value(input)));
        }
        
        if (input.contains(" ")) {
            return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.matchPhrase(mp -> mp.field(field).query(input)));
        }
        
        return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.match(m -> m.field(field).query(input)));
    }
}
