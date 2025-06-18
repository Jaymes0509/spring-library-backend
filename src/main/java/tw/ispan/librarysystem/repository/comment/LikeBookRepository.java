package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.comment.LikeBook;

import java.util.Optional;

@Repository
public interface LikeBookRepository extends JpaRepository<LikeBook, Long> {

    Optional<LikeBook> findByCommentIdAndMemberId(Integer commentId, Integer memberId);

    void deleteByCommentIdAndMemberId(Integer commentId, Integer memberId);

    long countByCommentId(Integer commentId);
}
