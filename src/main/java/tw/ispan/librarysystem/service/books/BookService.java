package tw.ispan.librarysystem.service.books;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import tw.ispan.librarysystem.dto.SearchCondition;
import tw.ispan.librarysystem.dto.BookSimpleDTO;
import tw.ispan.librarysystem.dto.BookDTO;
import tw.ispan.librarysystem.entity.books.BookDetailEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.entity.books.CategoryEntity;
import tw.ispan.librarysystem.entity.books.CategorySystemEntity;
import tw.ispan.librarysystem.repository.books.BookDetailRepository;
import tw.ispan.librarysystem.repository.books.BookRepository;
import tw.ispan.librarysystem.mapper.BookMapper;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDetailRepository bookDetailRepository;

    @Autowired
    private BookMapper bookMapper;

    /**
     * 根據書籍與外部 API 資訊，儲存或更新書籍詳細資料（封面與摘要）
     */
    public void updateBookDetail(BookEntity book, String coverUrl, String summary) {
        try {
            Optional<BookDetailEntity> existing = bookDetailRepository.findByBook(book);
            BookDetailEntity detail = existing.orElseGet(BookDetailEntity::new);
            detail.setBook(book);
            detail.setImgUrl(coverUrl);
            detail.setSummary(summary);
            bookDetailRepository.save(detail);
        } catch (Exception e) {
            System.err.println("更新書籍封面與摘要失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢全部書籍，分頁
     */
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * 根據 ID 查詢單筆書籍
     */
    public Optional<BookEntity> findById(Integer id) {
        return bookRepository.findById(id);
    }

    /**
     * 新增或更新書籍
     */
    public BookEntity save(BookEntity book) {
        return bookRepository.save(book);
    }

    /**
     * 刪除書籍
     */
    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }

    /**
     * 根據 ISBN 查詢書籍
     */
    public Optional<BookEntity> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * 簡易搜尋：只查 title, author, isbn, imgUrl
     */
    public Page<BookSimpleDTO> simpleSearch(String field, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return bookRepository.simpleSearchWithCover(keyword.trim(), pageable);
    }

    /**
     * 進階搜尋：回傳 Page<BookDTO>
     */
    public Page<BookDTO> advancedSearch(List<SearchCondition> conditions, Pageable pageable) {
        Specification<BookEntity> spec = buildSpecification(conditions);
        Page<BookEntity> entityPage = bookRepository.findAll(spec, pageable);
        // 用 Mapper 轉換
        List<BookDTO> dtoList = entityPage.getContent().stream().map(bookMapper::toDTO).toList();
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    private Specification<BookEntity> buildSpecification(List<SearchCondition> conditions) {
        return new Specification<BookEntity>() {
            @Override
            public Predicate toPredicate(Root<BookEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();

                for (SearchCondition cond : conditions) {
                    Predicate p = null;
                    JsonNode valNode = cond.getValue();

                    switch (cond.getField()) {
                        case "title":
                            String title = valNode.asText();
                            p = cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
                            break;
                        case "author":
                            String author = valNode.asText();
                            p = cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
                            break;
                        case "publisher":
                            String publisher = valNode.asText();
                            p = cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
                            break;
                        case "isbn":
                            String isbn = valNode.asText();
                            p = cb.like(cb.lower(root.get("isbn")), "%" + isbn.toLowerCase() + "%");
                            break;
                        case "classification":
                            String cls = valNode.asText().toLowerCase();
                            p = cb.like(cb.lower(root.get("classification")),"%" + cls + "%");
                            break;
                        case "publishdate":
                            Integer from = (valNode.has("from") && !valNode.get("from").asText().isEmpty()) ? valNode.get("from").asInt() : null;
                            Integer to = (valNode.has("to") && !valNode.get("to").asText().isEmpty()) ? valNode.get("to").asInt() : null;
                            Predicate datePred = null;
                            if (from != null) {
                                datePred = cb.greaterThanOrEqualTo(root.get("publishdate"), from);
                            }
                            if (to != null) {
                                Predicate toPred = cb.lessThanOrEqualTo(root.get("publishdate"), to);
                                datePred = (datePred == null) ? toPred : cb.and(datePred, toPred);
                            }
                            p = datePred;
                            break;
                        case "categorysystem":
                            String csId = valNode.get("cs_id").asText();
                            Join<BookEntity, CategoryEntity> catJoin = root.join("category", JoinType.INNER);
                            Join<CategoryEntity, CategorySystemEntity> sysJoin = catJoin.join("categorysystem", JoinType.INNER);
                            p = cb.equal(cb.lower(sysJoin.get("csCode")), csId.toLowerCase());
                            break;
                        case "language":
                            String lang = valNode.asText();
                            p = cb.equal(cb.lower(root.get("language")), lang.toLowerCase());
                            break;
                        default:
                            break;
                        case "version":
                            String ver = valNode.asText();
                            p = cb.equal(cb.lower(root.get("version")), ver.toLowerCase());
                            break;
                    }

                    if (p != null) {
                        switch (cond.getOperator().toUpperCase()) {
                            case "AND":
                                predicate = cb.and(predicate, p);
                                break;
                            case "OR":
                                predicate = cb.or(predicate, p);
                                break;
                            case "NOT":
                                predicate = cb.and(predicate, cb.not(p));
                                break;
                        }
                    }
                }
                return predicate;
            }
        };
    }
}
