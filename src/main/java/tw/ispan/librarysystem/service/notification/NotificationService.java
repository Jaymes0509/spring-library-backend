package tw.ispan.librarysystem.service.notification;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.notification.Notification;
import tw.ispan.librarysystem.repository.notification.NotificationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;

    public void sendRenewalNotification(Long userId, String bookTitle, LocalDateTime newDueDate) {
        logger.info("發送續借成功通知 - 使用者ID: {}, 書籍: {}", userId, bookTitle);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("RENEWAL_SUCCESS");
        notification.setTitle("續借成功通知");
        notification.setContent(String.format(
            "您已成功續借《%s》，新的到期日為 %s",
            bookTitle,
            newDueDate.toString()
        ));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
        logger.info("續借通知已發送");
    }
} 