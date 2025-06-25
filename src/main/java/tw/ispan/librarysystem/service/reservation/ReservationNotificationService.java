package tw.ispan.librarysystem.service.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.entity.books.BookEntity;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReservationNotificationService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US);

    /**
     * 過濾字符串，只保留安全的 ASCII 字符
     * @param input 輸入字符串
     * @return 過濾後的字符串
     */
    private String sanitizeString(String input) {
        if (input == null) {
            return "Unknown";
        }
        
        // 移除或替換可能導致問題的字符
        return input.replaceAll("[^\\x20-\\x7E]", "") // 只保留可打印的 ASCII 字符
                   .replaceAll("[\\r\\n\\t]", " ") // 替換換行符和製表符
                   .trim();
    }

    /**
     * 發送預約成功通知郵件（統一方法）
     * @param reservation 單本預約實體
     */
    public void sendReservationSuccessEmail(ReservationEntity reservation) {
        // 根據 userId 查找會員資訊
        Member member = memberRepository.findById(reservation.getUserId().longValue()).orElse(null);
        
        if (member == null) {
            throw new RuntimeException("找不到會員資訊，無法發送通知郵件");
        }

        sendReservationSuccessEmail(member, List.of(reservation), null);
    }

    /**
     * 發送預約成功通知郵件（統一方法）
     * @param member 會員實體
     * @param reservations 預約實體列表（可以是單本或多本）
     * @param batchId 批次編號（可選，批量預約時使用）
     */
    public void sendReservationSuccessEmail(Member member, List<ReservationEntity> reservations, String batchId) {
        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(member.getEmail());
            
            // 根據預約數量決定郵件主旨
            if (reservations.size() == 1) {
                emailMessage.setSubject("圖書館預約確認通知");
            } else {
                emailMessage.setSubject("圖書館批量預約確認通知");
            }
            
            // 構建郵件內容
            String emailContent = buildReservationSuccessEmailContent(member, reservations, batchId);
            emailMessage.setText(emailContent);
            emailMessage.setFrom("ispanlibrarysystem@gmail.com");
            
            mailSender.send(emailMessage);
        } catch (Exception e) {
            System.err.println("發送預約成功通知郵件失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 發送批量預約成功通知郵件（保持向後兼容）
     * @param member 會員實體
     * @param reservations 預約實體列表
     * @param batchId 批次編號
     */
    public void sendBatchReservationSuccessEmail(Member member, List<ReservationEntity> reservations, String batchId) {
        sendReservationSuccessEmail(member, reservations, batchId);
    }

    /**
     * 構建預約成功通知郵件內容（統一方法）
     * @param member 會員實體
     * @param reservations 預約實體列表
     * @param batchId 批次編號（可選）
     * @return 郵件內容
     */
    private String buildReservationSuccessEmailContent(Member member, List<ReservationEntity> reservations, String batchId) {
        StringBuilder content = new StringBuilder();
        content.append("親愛的 ").append(sanitizeString(member.getName())).append(" 您好，\n\n");
        
        if (reservations.size() == 1) {
            // 單本預約
            content.append("您的圖書預約已成功建立！\n\n");
            content.append("預約詳細資訊：\n");
            
            ReservationEntity reservation = reservations.get(0);
            BookEntity book = reservation.getBook();
            
            content.append("預約編號：").append(reservation.getReservationId()).append("\n");
            content.append("書籍標題：").append(book != null ? sanitizeString(book.getTitle()) : "未知").append("\n");
            content.append("作者：").append(book != null ? sanitizeString(book.getAuthor()) : "未知").append("\n");
            content.append("ISBN：").append(book != null ? sanitizeString(book.getIsbn()) : "未知").append("\n");
            content.append("預約時間：").append(reservation.getReserveTime() != null ? 
                reservation.getReserveTime().format(DATE_FORMATTER) : "未知").append("\n");
            content.append("取書期限：").append(reservation.getExpiryDate() != null ? 
                reservation.getExpiryDate().format(DATE_FORMATTER) : "未知").append("\n");
            
            if (reservation.getPickupLocation() != null) {
                content.append("取書地點：").append(sanitizeString(reservation.getPickupLocation())).append("\n");
            }
            if (reservation.getPickupMethod() != null) {
                content.append("取書方式：").append(sanitizeString(reservation.getPickupMethod())).append("\n");
            }
        } else {
            // 批量預約
            content.append("您的批量圖書預約已成功建立！\n\n");
            content.append("批量預約詳細資訊：\n");
            if (batchId != null) {
                content.append("批次編號：").append(sanitizeString(batchId)).append("\n");
            }
            content.append("預約書籍數量：").append(reservations.size()).append(" 本\n\n");
            
            content.append("預約書籍清單：\n");
            for (int i = 0; i < reservations.size(); i++) {
                ReservationEntity reservation = reservations.get(i);
                BookEntity book = reservation.getBook();
                
                content.append(i + 1).append(". 預約編號：").append(reservation.getReservationId()).append("\n");
                content.append("   書籍標題：").append(book != null ? sanitizeString(book.getTitle()) : "未知").append("\n");
                content.append("   作者：").append(book != null ? sanitizeString(book.getAuthor()) : "未知").append("\n");
                content.append("   ISBN：").append(book != null ? sanitizeString(book.getIsbn()) : "未知").append("\n");
                content.append("   取書期限：").append(reservation.getExpiryDate() != null ? 
                    reservation.getExpiryDate().format(DATE_FORMATTER) : "未知").append("\n\n");
            }
            
            if (!reservations.isEmpty()) {
                ReservationEntity firstReservation = reservations.get(0);
                if (firstReservation.getPickupLocation() != null) {
                    content.append("取書地點：").append(sanitizeString(firstReservation.getPickupLocation())).append("\n");
                }
                if (firstReservation.getPickupMethod() != null) {
                    content.append("取書方式：").append(sanitizeString(firstReservation.getPickupMethod())).append("\n");
                }
            }
        }
        
        content.append("\n重要提醒：\n");
        if (reservations.size() == 1) {
            content.append("1. 請在期限內取書，逾期將自動取消預約\n");
        } else {
            content.append("1. 請在各自期限內取書，逾期將自動取消預約\n");
        }
        content.append("2. 取書時請攜帶有效身分證件\n");
        content.append("3. 如有任何問題，請聯繫圖書館服務台\n\n");
        
        content.append("感謝您使用我們的圖書館服務！\n\n");
        content.append("此為系統自動發送之郵件，請勿回覆。\n");
        
        return content.toString();
    }
} 