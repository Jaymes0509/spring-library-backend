package tw.ispan.librarysystem.entity.comment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookcommentlikes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "member_id"}))
public class LikeBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @Column(name = "comment_id", nullable = false)
    private Integer commentId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "liked_at")
    private LocalDateTime likedAt = LocalDateTime.now();

    // Getter 和 Setter
    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}
