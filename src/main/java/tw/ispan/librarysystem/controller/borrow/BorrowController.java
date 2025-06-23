package tw.ispan.librarysystem.controller.borrow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.entity.borrow.Borrow;
import tw.ispan.librarysystem.service.borrow.BorrowService;
import tw.ispan.librarysystem.service.member.MemberService;
import tw.ispan.librarysystem.security.CheckJwt;
import tw.ispan.librarysystem.util.JwtTool;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.dto.borrow.BorrowRequestDto;
import tw.ispan.librarysystem.dto.borrow.BorrowResponseDto;
import tw.ispan.librarysystem.dto.borrow.BorrowStatisticsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class BorrowController {
    private static final Logger logger = LoggerFactory.getLogger(BorrowController.class);
    private final BorrowService borrowService;
    private final MemberService memberService;

    public BorrowController(BorrowService borrowService, MemberService memberService) {
        this.borrowService = borrowService;
        this.memberService = memberService;
    }

    /**
     * 借書
     */
    @PostMapping("/borrow")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> borrowBook(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BorrowRequestDto borrowRequest) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到借書請求 - 使用者ID: {}, 書籍ID: {}", userId, borrowRequest.getBookId());
            
            // 執行借書
            Borrow borrow = borrowService.borrowBook(userId, borrowRequest.getBookId());
            
            // 建立回應
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "借書成功");
            response.put("data", new BorrowResponseDto(borrow));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("借書失敗", e);
            return createErrorResponse(e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("借書時發生未預期錯誤", e);
            return createErrorResponse("借書失敗，請稍後再試", 500);
        }
    }

    /**
     * 還書
     */
    @PostMapping("/return/{borrowId}")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> returnBook(@PathVariable Integer borrowId) {
        try {
            logger.info("收到還書請求 - 借閱ID: {}", borrowId);
            Borrow borrow = borrowService.returnBook(borrowId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "還書成功");
            response.put("data", new BorrowResponseDto(borrow));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("還書失敗", e);
            return createErrorResponse(e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("還書時發生未預期錯誤", e);
            return createErrorResponse("還書失敗，請稍後再試", 500);
        }
    }

    /**
     * 續借
     */
    @PostMapping("/renew/{borrowId}")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> renewBook(@PathVariable Integer borrowId) {
        try {
            logger.info("收到續借請求 - 借閱ID: {}", borrowId);
            Borrow borrow = borrowService.renewBook(borrowId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "續借成功");
            response.put("data", new BorrowResponseDto(borrow));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("續借失敗", e);
            return createErrorResponse(e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("續借時發生未預期錯誤", e);
            return createErrorResponse("續借失敗，請稍後再試", 500);
        }
    }

    /**
     * 獲取借閱歷史
     */
    @GetMapping("/history")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> getMemberBorrowHistory(
        @RequestHeader("Authorization") String authHeader,
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean withPagination
    ) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到獲取會員借閱歷史請求 - 使用者ID: {}", userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "獲取借閱歷史成功");
            
            if (withPagination) {
                Page<Borrow> borrows = borrowService.getMemberBorrowHistory(userId, pageable);
                response.put("data", borrows.map(BorrowResponseDto::new));
                response.put("pagination", createPaginationInfo(borrows));
            } else {
                List<Borrow> borrows = borrowService.getMemberBorrowHistory(userId);
                response.put("data", borrows.stream().map(BorrowResponseDto::new).toList());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取借閱歷史失敗", e);
            return createErrorResponse("獲取借閱歷史失敗，請稍後再試", 500);
        }
    }

    /**
     * 獲取當前借閱
     */
    @GetMapping("/current")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> getMemberCurrentBorrows(@RequestHeader("Authorization") String authHeader) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到獲取會員當前借閱請求 - 使用者ID: {}", userId);
            
            List<Borrow> borrows = borrowService.getMemberCurrentBorrows(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "獲取當前借閱成功");
            response.put("data", borrows.stream().map(BorrowResponseDto::new).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取當前借閱失敗", e);
            return createErrorResponse("獲取當前借閱失敗，請稍後再試", 500);
        }
    }

    /**
     * 獲取逾期借閱
     */
    @GetMapping("/overdue")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> getMemberOverdueBorrows(@RequestHeader("Authorization") String authHeader) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到獲取會員逾期借閱請求 - 使用者ID: {}", userId);
            
            List<Borrow> borrows = borrowService.getMemberOverdueBorrows(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "獲取逾期借閱成功");
            response.put("data", borrows.stream().map(BorrowResponseDto::new).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取逾期借閱失敗", e);
            return createErrorResponse("獲取逾期借閱失敗，請稍後再試", 500);
        }
    }

    /**
     * 檢查是否可續借
     */
    @GetMapping("/renew/{borrowId}/check")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> checkRenewable(@PathVariable Integer borrowId) {
        try {
            logger.info("收到檢查是否可續借請求 - 借閱ID: {}", borrowId);
            boolean canRenew = borrowService.canRenew(borrowId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "檢查續借資格成功");
            response.put("data", Map.of("canRenew", canRenew));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("檢查續借資格失敗", e);
            return createErrorResponse(e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("檢查續借資格時發生未預期錯誤", e);
            return createErrorResponse("檢查續借資格失敗，請稍後再試", 500);
        }
    }

    /**
     * 獲取借閱統計
     */
    @GetMapping("/statistics")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> getBorrowStatistics(@RequestHeader("Authorization") String authHeader) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到獲取借閱統計請求 - 使用者ID: {}", userId);
            
            BorrowStatisticsDto statistics = borrowService.getBorrowStatistics(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "獲取借閱統計成功");
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取借閱統計失敗", e);
            return createErrorResponse("獲取借閱統計失敗，請稍後再試", 500);
        }
    }

    /**
     * 檢查借閱限制
     */
    @GetMapping("/check-limits")
    @CheckJwt
    public ResponseEntity<Map<String, Object>> checkBorrowLimits(@RequestHeader("Authorization") String authHeader) {
        try {
            // 從JWT token中獲取使用者email
            String token = authHeader.replace("Bearer ", "");
            String email = JwtTool.parseToken(token);
            
            // 根據email獲取會員資訊
            Member member = memberService.getMemberByEmail(email);
            if (member == null) {
                return createErrorResponse("找不到會員資訊", 400);
            }
            
            Integer userId = member.getId();
            logger.info("收到檢查借閱限制請求 - 使用者ID: {}", userId);
            
            Map<String, Object> limits = borrowService.checkBorrowLimits(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "檢查借閱限制成功");
            response.put("data", limits);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("檢查借閱限制失敗", e);
            return createErrorResponse("檢查借閱限制失敗，請稍後再試", 500);
        }
    }

    // 私有方法：建立錯誤回應
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(response);
    }

    // 私有方法：建立分頁資訊
    private Map<String, Object> createPaginationInfo(Page<Borrow> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        return pagination;
    }
} 