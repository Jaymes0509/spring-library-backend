package tw.ispan.librarysystem.service.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.ispan.librarysystem.entity.comment.LikeBook;
import tw.ispan.librarysystem.repository.comment.LikeBookRepository;

import java.util.Optional;

@Service
public class LikeBookServiceImpl implements LikeBookService {

    @Autowired
    private LikeBookRepository likeBookRepository;

    @Override
    @Transactional
    public boolean likeComment(Integer commentId, Integer userId) {
        try {
            System.out.println("[LikeBookService] likeComment called with commentId=" + commentId + ", userId=" + userId);

            Optional<LikeBook> existing = likeBookRepository.findByCommentIdAndUserId(commentId, userId);
            if (existing.isPresent()) {
                System.out.println("[LikeBookService] User has already liked this comment.");
                return false;
            }

            LikeBook like = new LikeBook();
            like.setCommentId(commentId);
            like.setUserId(userId);

            likeBookRepository.save(like);

            System.out.println("[LikeBookService] Like saved successfully.");

            return true;

        } catch (Exception e) {
            System.err.println("[LikeBookService] Error saving like: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unlikeComment(Integer commentId, Integer userId) {
        try {
            System.out.println("[LikeBookService] unlikeComment called with commentId=" + commentId + ", userId=" + userId);

            Optional<LikeBook> existing = likeBookRepository.findByCommentIdAndUserId(commentId, userId);
            if (existing.isEmpty()) {
                System.out.println("[LikeBookService] No like record found to delete.");
                return false;
            }

            likeBookRepository.deleteByCommentIdAndUserId(commentId, userId);

            System.out.println("[LikeBookService] Like removed successfully.");

            return true;

        } catch (Exception e) {
            System.err.println("[LikeBookService] Error removing like: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long countLikes(Integer commentId) {
        return likeBookRepository.countByCommentId(commentId);
    }

    @Override
    public boolean hasUserLiked(Integer commentId, Integer userId) {
        return likeBookRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }
}
