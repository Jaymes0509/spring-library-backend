package tw.ispan.librarysystem.service.violation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.entity.violation.ViolationRecord;
import tw.ispan.librarysystem.repository.violation.ViolationRecordRepository;
import tw.ispan.librarysystem.dto.violation.ViolationRecordDTO;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViolationRecordService {

    @Autowired
    private ViolationRecordRepository repository;
    
    @Autowired
    private MemberRepository memberRepository;

    public ViolationRecord addViolation(Integer userId, String violationType) {
        long count = repository.countByUserId(userId);

        int days = 180; // 借閱違規預設懲罰天數
        if (violationType.equals("預約了沒拿") && count >= 2) {
            days = 60; // 預約違規滿 3 次，懲罰 60 天
        }

        ViolationRecord record = new ViolationRecord();
        record.setUserId(userId);
        record.setViolationType(violationType);
        record.setViolationDate(LocalDateTime.now());
        record.setPenaltyEndDate(LocalDateTime.now().plusDays(days));
        return repository.save(record);
    }

    public List<ViolationRecord> getViolationsByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public boolean isUserSuspended(Integer userId) {
        return repository.existsByUserIdAndPenaltyEndDateAfter(userId, LocalDateTime.now());
    }

    /**
     * 獲取所有違規記錄（管理員功能）
     * @return 包含用戶資訊的違規記錄列表
     */
    public List<ViolationRecordDTO> getAllViolations() {
        List<ViolationRecord> violations = repository.findAll();
        
        return violations.stream().map(violation -> {
            ViolationRecordDTO dto = new ViolationRecordDTO(
                violation.getViolationId(),
                violation.getUserId(),
                violation.getViolationType(),
                violation.getViolationDate(),
                violation.getPenaltyEndDate()
            );
            
            // 獲取用戶資訊
            Member member = memberRepository.findById(violation.getUserId().longValue()).orElse(null);
            if (member != null) {
                dto.setUserName(member.getName());
                dto.setUserEmail(member.getEmail());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 獲取活躍的違規記錄（懲罰期尚未結束）
     * @return 活躍的違規記錄列表
     */
    public List<ViolationRecordDTO> getActiveViolations() {
        List<ViolationRecord> violations = repository.findAll();
        
        return violations.stream()
            .filter(violation -> violation.getPenaltyEndDate().isAfter(LocalDateTime.now()))
            .map(violation -> {
                ViolationRecordDTO dto = new ViolationRecordDTO(
                    violation.getViolationId(),
                    violation.getUserId(),
                    violation.getViolationType(),
                    violation.getViolationDate(),
                    violation.getPenaltyEndDate()
                );
                
                // 獲取用戶資訊
                Member member = memberRepository.findById(violation.getUserId().longValue()).orElse(null);
                if (member != null) {
                    dto.setUserName(member.getName());
                    dto.setUserEmail(member.getEmail());
                }
                
                return dto;
            }).collect(Collectors.toList());
    }
} 