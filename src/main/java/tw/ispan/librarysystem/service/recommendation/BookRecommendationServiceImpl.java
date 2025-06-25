package tw.ispan.librarysystem.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import tw.ispan.librarysystem.dto.recommendation.BookRecommendationDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;
import tw.ispan.librarysystem.repository.member.MemberRepository;
import tw.ispan.librarysystem.repository.recommendation.BookRecommendationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRecommendationServiceImpl implements BookRecommendationService {

    private final BookRecommendationRepository repository;

    @Override
    public BookRecommendation submitRecommendation(BookRecommendationDto dto, Member member) {
        BookRecommendation recommendation = new BookRecommendation();
        BeanUtils.copyProperties(dto, recommendation);
        recommendation.setUserId(member.getUserId()); // 把 Member 的 id 設進去
        return repository.save(recommendation);
    }



    @Override
    public List<BookRecommendation> findAll() {
        return repository.findAll();
    }

    @Override
    public void updateStatus(Long id, BookRecommendation.Status status) {
        BookRecommendation rec = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        rec.setStatus(status);
        repository.save(rec);
    }

    @Override
    public int getUserCount(Member member) {
        return repository.countByUserId(member.getUserId()); // 假設你有 getUserId() 方法
    }
}
