package tw.ispan.librarysystem.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.comment.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, Integer> {

    Optional<BookComment> findByBookIdAndUserId(Integer bookId, Integer userId);

    List<BookComment> findByBookIdOrderByCreatedAtDesc(Integer bookId);

    // 新增此方法：依 userId 查詢該用戶所有書評（時間倒序）
    List<BookComment> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
