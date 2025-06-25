package tw.ispan.librarysystem.service.recommendation;


import tw.ispan.librarysystem.dto.recommendation.BookRecommendationDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;

import java.util.List;

public interface BookRecommendationService {
    BookRecommendation submitRecommendation(BookRecommendationDto dto, Member member);

    List<BookRecommendation> findAll();

    void updateStatus(Long id, BookRecommendation.Status status);

    int getUserCount(Member member);

}
