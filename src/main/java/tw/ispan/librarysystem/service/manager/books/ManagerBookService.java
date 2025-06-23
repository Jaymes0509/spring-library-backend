package tw.ispan.librarysystem.service.manager.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import tw.ispan.librarysystem.entity.books.BookDetailEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.repository.books.BookDetailRepository;
import tw.ispan.librarysystem.repository.manager.books.ManagerBookRepository;
import tw.ispan.librarysystem.dto.SearchCondition;
import jakarta.persistence.criteria.Predicate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerBookService {

    @Autowired
    private ManagerBookRepository bookRepository;

    // 進階搜尋
    public Page<BookEntity> searchBooks(String title, String author, String publisher, String isbn,
                                        String classification, LocalDate yearFrom, LocalDate yearTo, String language, Pageable pageable) {
        return bookRepository.searchBooks(title, author, publisher, isbn, classification, yearFrom, yearTo, language,
                pageable);
    }

    @Autowired
    private BookDetailRepository bookDetailRepository;

    /**
     * 根據書籍與外部 API 資訊，儲存或更新書籍詳細資料（封面與摘要）
     */
    public void updateBookDetail(BookEntity book, String coverUrl, String summary) {
        try {
            // 先查詢是否已有該書的詳細資料
            Optional<BookDetailEntity> existing = bookDetailRepository.findByBook(book);

            BookDetailEntity detail = existing.orElseGet(BookDetailEntity::new);
            detail.setBook(book); // 綁定主鍵（@MapsId）
            detail.setImgUrl(coverUrl);
            detail.setSummary(summary);

            bookDetailRepository.save(detail); // 寫入資料庫
        } catch (Exception e) {
            System.err.println("更新書籍封面與摘要失敗: " + e.getMessage());
        }
    }

    // 查詢全部
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    // 查詢單筆
    public Optional<BookEntity> findById(Integer id) {
        return bookRepository.findById(id);
    }

    // 新增或修改
    public BookEntity save(BookEntity book) {
        return bookRepository.save(book);
    }

    // 刪除
    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }

    public Optional<BookEntity> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Page<BookEntity> advancedSearch(List<SearchCondition> conditions, Pageable pageable) {
        Specification<BookEntity> spec = buildSpecification(conditions);
        return bookRepository.findAll(spec, pageable);
    }

    private Specification<BookEntity> buildSpecification(List<SearchCondition> conditions) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchCondition cond : conditions) {
                Predicate p = null;

                String valueStr = cond.getValue().asText();
                switch (cond.getField()) {

                    case "title":
                        p = cb.like(cb.lower(root.get("title")), "%" + valueStr.toLowerCase() + "%");
                        break;
                    case "author":
                        p = cb.like(cb.lower(root.get("author")), "%" + valueStr.toLowerCase() + "%");
                        break;
                    case "publisher":
                        p = cb.like(cb.lower(root.get("publisher")), "%" + valueStr.toLowerCase() + "%");
                        break;
                    case "isbn":
                        p = cb.like(cb.lower(root.get("isbn")), "%" + valueStr.toLowerCase() + "%");
                        break;
                    case "classification":
                        p = cb.equal(root.get("classification"), valueStr);
                        break;
                    case "publishdate":
                        try {
                            var node = objectMapper.readTree(valueStr.toString());

                            String from = node.has("from") ? node.get("from").asText() : null;
                            String to = node.has("to") ? node.get("to").asText() : null;
                            if (from != null && !from.isEmpty()) {
                                p = cb.greaterThanOrEqualTo(cb.substring(root.get("publishdate"), 1, 4), from);
                            }
                            if (to != null && !to.isEmpty()) {
                                Predicate toPredicate = cb
                                        .lessThanOrEqualTo(cb.substring(root.get("publishdate"), 1, 4), to);
                                p = (p == null) ? toPredicate : cb.and(p, toPredicate);
                            }
                        } catch (Exception e) {
                            // 解析失敗可略過
                        }
                        break;
                    case "categorysystem":
                        try {

                            var json = objectMapper.readTree(valueStr);

                            if (json.has("cs_id")) {
                                String csId = json.get("cs_id").asText();
                                p = cb.equal(cb.lower(root.get("category").get("system").get("code")),
                                        csId.toLowerCase());
                            }
                        } catch (Exception e) {
                            // JSON 格式錯誤略過處理
                        }
                        break;
                    // 其他欄位可依需求擴充
                }
                if (p != null) {
                    if ("AND".equalsIgnoreCase(cond.getOperator())) {
                        predicate = cb.and(predicate, p);
                    } else if ("OR".equalsIgnoreCase(cond.getOperator())) {
                        predicate = cb.or(predicate, p);
                    } else if ("NOT".equalsIgnoreCase(cond.getOperator())) {
                        predicate = cb.and(predicate, cb.not(p));
                    }
                }
            }
            return predicate;
        };
    }

    public Page<BookEntity> simpleSearch(String field, String keyword, Pageable pageable) {
        Specification<BookEntity> spec = (root, query, cb) -> {
            switch (field) {
                case "title":
                    return cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
                case "author":
                    return cb.like(cb.lower(root.get("author")), "%" + keyword.toLowerCase() + "%");
                case "publisher":
                    return cb.like(cb.lower(root.get("publisher")), "%" + keyword.toLowerCase() + "%");
                case "isbn":
                    return cb.like(cb.lower(root.get("isbn")), "%" + keyword.toLowerCase() + "%");
                case "classification":
                    return cb.equal(root.get("classification"), keyword);
                case "categorysystem":
                    return cb.like(cb.lower(root.get("categorysystem").get("csName")),
                            "%" + keyword.toLowerCase() + "%");
                // 其他欄位可依需求擴充
                default:
                    return cb.conjunction();
            }
        };
        return bookRepository.findAll(spec, pageable);
    }

}