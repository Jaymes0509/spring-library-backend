package tw.ispan.librarysystem.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.ispan.librarysystem.entity.notification.Notification;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Integer userId, Boolean isRead);
    long countByUserIdAndIsRead(Integer userId, Boolean isRead);
} 