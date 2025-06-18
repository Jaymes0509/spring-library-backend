package tw.ispan.librarysystem.controller.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.ispan.librarysystem.entity.comment.RandomBook;
import tw.ispan.librarysystem.repository.comment.RandomBookRepository;

import java.util.List;

@RestController
public class RandomBookController {

    @Autowired
    private RandomBookRepository randomBookRepository;

    @GetMapping("/api/random-books")
    public List<RandomBook> getRandomBooks() {
        return randomBookRepository.findRandomTenBooks();
    }
}
