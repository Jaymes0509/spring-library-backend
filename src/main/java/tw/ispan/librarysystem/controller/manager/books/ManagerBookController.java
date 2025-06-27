//package tw.ispan.librarysystem.controller.manager.books;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import tw.ispan.librarysystem.dto.BookDTO;
//import tw.ispan.librarysystem.dto.PageResponseDTO;
//import tw.ispan.librarysystem.dto.SearchCondition;
//import tw.ispan.librarysystem.entity.books.BookEntity;
//import tw.ispan.librarysystem.mapper.BookMapper;
//// import tw.ispan.librarysystem.repository.manager.books.ManagerBookRepository;
//import tw.ispan.librarysystem.service.books.BookDetailService;
//import tw.ispan.librarysystem.service.manager.books.ManagerBookService;
//
//@RestController
//@RequestMapping("/api/manager/books")
//public class ManagerBookController {
//
//    @Autowired
//    private ManagerBookService bookService;
//
//    @Autowired
//    private BookMapper bookMapper;
//
//    @Autowired
//    private BookDetailService bookDetailService;
//
//    @PostMapping("/fill-details")
//    public ResponseEntity<String> fillMissingBookDetails() {
//        bookDetailService.updateMissingCoversAndSummaries();
//        return ResponseEntity.ok("補齊完成！");
//    }
//
//    @GetMapping("/{bookId}")
//    public BookDTO getBookById(@PathVariable Integer bookId) {
//        BookEntity book = bookService.findById(bookId).orElse(null);
//        return bookMapper.toDTO(book);
//    }
//
//    @GetMapping("/isbn/{isbn}")
//    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
//        Optional<BookEntity> optional = bookService.findByIsbn(isbn);
//        if (optional.isPresent()) {
//            BookDTO dto = bookMapper.toDTO(optional.get());
//            return ResponseEntity.ok(dto);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/simple-search")
//    public PageResponseDTO<BookDTO> simpleSearch(
//            @RequestParam String field,
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "title") String sortField,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
//        Page<BookEntity> bookPage = bookService.simpleSearch(field, keyword, pageable);
//        return bookMapper.toPageDTO(bookPage);
//    }
//
//    @PostMapping("/advanced-search")
//    public PageResponseDTO<BookDTO> advancedSearch(
//            @RequestBody List<SearchCondition> conditions,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "title") String sortField,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
//        Page<BookEntity> bookPage = bookService.advancedSearch(conditions, pageable);
//        return bookMapper.toPageDTO(bookPage);
//    }
//
//    @GetMapping("/all")
//    public Page<BookDTO> getBooksPage(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<BookEntity> bookPage = bookService.findAll(pageable);
//        return bookPage.map(bookMapper::toDTO);
//    }
//
//    @GetMapping("/test")
//    public String test() {
//        return "test";
//    }
//}