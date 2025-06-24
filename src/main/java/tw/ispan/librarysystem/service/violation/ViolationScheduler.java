package tw.ispan.librarysystem.service.violation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ViolationScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ViolationRecordService violationService;

    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨 3:00 執行
    @Transactional
    public void checkOverdueViolations() {
        // 借閱逾期（2 件以上）
        String borrowSql = "SELECT user_id, COUNT(*) AS overdue_count FROM borrow_records WHERE due_date < ? AND return_date IS NULL GROUP BY user_id HAVING COUNT(*) >= 2";
        jdbcTemplate.queryForStream(borrowSql, (rs, rowNum) -> rs.getInt("user_id"), LocalDate.now()).forEach(userId -> {
            LocalDateTime now = LocalDateTime.now();
            boolean alreadyExists = violationService
                    .getViolationsByUserId(userId).stream()
                    .anyMatch(v -> v.getViolationType().equals("借閱了沒還") &&
                            v.getViolationDate().toLocalDate().equals(now.toLocalDate()));
            if (!alreadyExists) {
                violationService.addViolation(userId, "借閱了沒還");
            }
        });

        // 預約違規：過期未領（expiry_date < now）且狀態為 "未領取"，統計當年度達 3 次
        String reserveSql = "SELECT user_id, COUNT(*) as cnt FROM reservations WHERE expiry_date < ? AND status = '未領取' AND YEAR(expiry_date) = YEAR(NOW()) GROUP BY user_id HAVING COUNT(*) >= 3";
        jdbcTemplate.queryForStream(reserveSql, (rs, rowNum) -> rs.getInt("user_id"), LocalDateTime.now()).forEach(userId -> {
            LocalDateTime now = LocalDateTime.now();
            boolean alreadyExists = violationService
                    .getViolationsByUserId(userId).stream()
                    .anyMatch(v -> v.getViolationType().equals("預約了沒拿") &&
                            v.getViolationDate().toLocalDate().equals(now.toLocalDate()));
            if (!alreadyExists) {
                violationService.addViolation(userId, "預約了沒拿");
            }
        });
    }
} 