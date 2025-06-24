package tw.ispan.librarysystem.service.comment;

public interface LikeBookService {
    boolean likeComment(Integer commentId, Integer userId);

    boolean unlikeComment(Integer commentId, Integer userId);

    long countLikes(Integer commentId);

    boolean hasUserLiked(Integer commentId, Integer userId);
}
