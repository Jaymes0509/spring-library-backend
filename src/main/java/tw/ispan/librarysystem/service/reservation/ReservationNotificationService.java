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
     * 發送預約成功通知郵件
     * @param reservation 預約實體
     */
    public void sendReservationSuccessEmail(ReservationEntity reservation) {
        // 根據 userId 查找會員資訊
        Member member = memberRepository.findById(reservation.getUserId().longValue()).orElse(null);
        
        if (member == null) {
            throw new RuntimeException("找不到會員資訊，無法發送通知郵件");
        }

        sendReservationSuccessEmail(member, reservation);
    }

    /**
     * 發送預約成功通知郵件（重載方法）
     * @param member 會員實體
     * @param reservation 預約實體
     */
    public void sendReservationSuccessEmail(Member member, ReservationEntity reservation) {
        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(member.getEmail());
            emailMessage.setSubject("Library Reservation Confirmation");
            
            // 構建郵件內容
            String emailContent = buildReservationSuccessEmailContent(member, reservation);
            emailMessage.setText(emailContent);
            emailMessage.setFrom("ispanlibrarysystem@gmail.com");
            
            mailSender.send(emailMessage);
        } catch (Exception e) {
            System.err.println("發送預約成功通知郵件失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 構建預約成功通知郵件內容
     * @param member 會員實體
     * @param reservation 預約實體
     * @return 郵件內容
     */
    private String buildReservationSuccessEmailContent(Member member, ReservationEntity reservation) {
        BookEntity book = reservation.getBook();
        
        StringBuilder content = new StringBuilder();
        content.append("Dear ").append(sanitizeString(member.getName())).append(",\n\n");
        content.append("Your book reservation has been successfully created!\n\n");
        
        content.append("Reservation Details:\n");
        content.append("Reservation ID: ").append(reservation.getReservationId()).append("\n");
        content.append("Book Title: ").append(book != null ? sanitizeString(book.getTitle()) : "Unknown").append("\n");
        content.append("Author: ").append(book != null ? sanitizeString(book.getAuthor()) : "Unknown").append("\n");
        content.append("ISBN: ").append(book != null ? sanitizeString(book.getIsbn()) : "Unknown").append("\n");
        content.append("Reservation Time: ").append(reservation.getReserveTime() != null ? 
            reservation.getReserveTime().format(DATE_FORMATTER) : "Unknown").append("\n");
        content.append("Pickup Deadline: ").append(reservation.getExpiryDate() != null ? 
            reservation.getExpiryDate().format(DATE_FORMATTER) : "Unknown").append("\n");
        
        if (reservation.getPickupLocation() != null) {
            content.append("Pickup Location: ").append(sanitizeString(reservation.getPickupLocation())).append("\n");
        }
        if (reservation.getPickupMethod() != null) {
            content.append("Pickup Method: ").append(sanitizeString(reservation.getPickupMethod())).append("\n");
        }
        
        content.append("\nImportant Reminders:\n");
        content.append("1. Please pick up your book before the deadline\n");
        content.append("2. Bring valid ID when picking up\n");
        content.append("3. Contact the library service desk for any questions\n\n");
        
        content.append("Thank you for using our library services!\n\n");
        content.append("This is an automated message, please do not reply.\n");
        
        return content.toString();
    }

    /**
     * 發送批量預約成功通知郵件
     * @param member 會員實體
     * @param reservations 預約實體列表
     * @param batchId 批次編號
     */
    public void sendBatchReservationSuccessEmail(Member member, java.util.List<ReservationEntity> reservations, String batchId) {
        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(member.getEmail());
            emailMessage.setSubject("Library Batch Reservation Confirmation");
            
            // 構建批量預約郵件內容
            String emailContent = buildBatchReservationSuccessEmailContent(member, reservations, batchId);
            emailMessage.setText(emailContent);
            emailMessage.setFrom("ispanlibrarysystem@gmail.com");
            
            mailSender.send(emailMessage);
        } catch (Exception e) {
            System.err.println("發送批量預約成功通知郵件失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 構建批量預約成功通知郵件內容
     * @param member 會員實體
     * @param reservations 預約實體列表
     * @param batchId 批次編號
     * @return 郵件內容
     */
    private String buildBatchReservationSuccessEmailContent(Member member, java.util.List<ReservationEntity> reservations, String batchId) {
        StringBuilder content = new StringBuilder();
        content.append("Dear ").append(sanitizeString(member.getName())).append(",\n\n");
        content.append("Your batch book reservation has been successfully created!\n\n");
        
        content.append("Batch Reservation Details:\n");
        content.append("Batch ID: ").append(sanitizeString(batchId)).append("\n");
        content.append("Number of Books: ").append(reservations.size()).append("\n\n");
        
        content.append("Reserved Books List:\n");
        for (int i = 0; i < reservations.size(); i++) {
            ReservationEntity reservation = reservations.get(i);
            BookEntity book = reservation.getBook();
            
            content.append(i + 1).append(". Reservation ID: ").append(reservation.getReservationId()).append("\n");
            content.append("   Book Title: ").append(book != null ? sanitizeString(book.getTitle()) : "Unknown").append("\n");
            content.append("   Author: ").append(book != null ? sanitizeString(book.getAuthor()) : "Unknown").append("\n");
            content.append("   ISBN: ").append(book != null ? sanitizeString(book.getIsbn()) : "Unknown").append("\n");
            content.append("   Pickup Deadline: ").append(reservation.getExpiryDate() != null ? 
                reservation.getExpiryDate().format(DATE_FORMATTER) : "Unknown").append("\n\n");
        }
        
        if (!reservations.isEmpty()) {
            ReservationEntity firstReservation = reservations.get(0);
            if (firstReservation.getPickupLocation() != null) {
                content.append("Pickup Location: ").append(sanitizeString(firstReservation.getPickupLocation())).append("\n");
            }
            if (firstReservation.getPickupMethod() != null) {
                content.append("Pickup Method: ").append(sanitizeString(firstReservation.getPickupMethod())).append("\n");
            }
        }
        
        content.append("\nImportant Reminders:\n");
        content.append("1. Please pick up your books before their respective deadlines\n");
        content.append("2. Bring valid ID when picking up\n");
        content.append("3. Contact the library service desk for any questions\n\n");
        
        content.append("Thank you for using our library services!\n\n");
        content.append("This is an automated message, please do not reply.\n");
        
        return content.toString();
    }
} 