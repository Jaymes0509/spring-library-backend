package tw.ispan.librarysystem.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.recommendation.BookRecommendation;

public interface BookRecommendationRepository extends JpaRepository<BookRecommendation, Long> {
    int countByUserId(Long userId);

}

