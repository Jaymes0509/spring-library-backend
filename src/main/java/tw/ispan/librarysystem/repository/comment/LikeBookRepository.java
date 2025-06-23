package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.comment.LikeBook;

import java.util.Optional;

@Repository
public interface LikeBookRepository extends JpaRepository<LikeBook, Long> {

    Optional<LikeBook> findByCommentIdAndUserId(Integer commentId, Integer userId);

    void deleteByCommentIdAndUserId(Integer commentId, Integer userId);

    long countByCommentId(Integer commentId);
}
