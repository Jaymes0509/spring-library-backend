package tw.ispan.librarysystem.service.recommendation;


import tw.ispan.librarysystem.dto.recommendation.BookRecommendationDto;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;

import java.util.List;
import java.util.Map;

public interface BookRecommendationService {
    BookRecommendation submitRecommendation(BookRecommendationDto dto, Member member);

    List<BookRecommendation> findAll();

    void updateStatus(Long id, BookRecommendation.Status status);

//    int getUserCount(Member member);

    Map<String, Integer> getUserCount(Member member);

    List<BookRecommendation> getUserRecommendations(Member member);


}
