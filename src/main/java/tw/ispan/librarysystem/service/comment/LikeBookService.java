package tw.ispan.librarysystem.service.comment;

public interface LikeBookService {
    boolean likeComment(Integer commentId, Integer memberId);

    boolean unlikeComment(Integer commentId, Integer memberId);

    long countLikes(Integer commentId);

    boolean hasUserLiked(Integer commentId, Integer memberId);
}
