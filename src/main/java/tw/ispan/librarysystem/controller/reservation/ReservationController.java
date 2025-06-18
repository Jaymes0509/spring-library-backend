// @SuppressWarnings("SpellCheckingInspection")
package tw.ispan.librarysystem.controller.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationBatchRequestDTO;
import tw.ispan.librarysystem.dto.reservation.ReservationResponseDTO;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;
import tw.ispan.librarysystem.service.ReservationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookreservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ReservationService reservationService;

    // 查詢用戶預約清單
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(@RequestParam String userId) {
        try {
            // 如果 userId 是 'current'，使用預設用戶 ID
            Integer actualUserId;
            if ("current".equals(userId)) {
                actualUserId = 1; // 預設用戶 ID，實際應用中應該從認證系統獲取
            } else {
                actualUserId = Integer.parseInt(userId);
            }
            
            List<ReservationDTO> reservations = reservationService.getReservationsByUserId(actualUserId);
            return ResponseEntity.ok(reservations);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 查詢單筆預約
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer reservationId) {
        return reservationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 查詢某本書的所有預約
    @GetMapping("/book/{bookId}")
    public List<ReservationDTO> getReservationsByBookId(@PathVariable Integer bookId) {
        return reservationService.getReservationsByBookId(bookId);
    }

    // 單本預約
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationDTO dto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
        result.setBookId(dto.getBookId());
        try {
            ReservationEntity entity = reservationService.createReservation(dto);
            result.setReservationId(entity.getReservationId());
            result.setStatus("success");
            response.setSuccess(true);
        } catch (Exception e) {
            result.setStatus("fail");
            result.setReason(e.getMessage());
            response.setSuccess(false);
        }
        results.add(result);
        response.setResults(results);
        return ResponseEntity.ok(response);
    }

    // 批量預約
    @PostMapping("/batch")
    public ResponseEntity<ReservationResponseDTO> batchReservation(@RequestBody ReservationBatchRequestDTO batchDto) {
        ReservationResponseDTO response = new ReservationResponseDTO();
        List<ReservationResponseDTO.Result> results = new ArrayList<>();
        boolean allSuccess = true;
        for (ReservationBatchRequestDTO.BookReserveItem item : batchDto.getBooks()) {
            ReservationResponseDTO.Result result = new ReservationResponseDTO.Result();
            result.setBookId(item.getBookId());
            try {
                ReservationDTO dto = new ReservationDTO();
                dto.setBookId(item.getBookId());
                dto.setUserId(batchDto.getUserId());
                dto.setStatus("PENDING");
                // reserve_time 轉型
                try {
                    dto.setReserveTime(java.time.LocalDateTime.parse(item.getReserveTime()));
                } catch (DateTimeParseException e) {
                    throw new RuntimeException("reserveTime 格式錯誤，請用 yyyy-MM-dd'T'HH:mm:ss");
                }
                ReservationEntity entity = reservationService.createReservation(dto);
                result.setReservationId(entity.getReservationId());
                result.setStatus("success");
            } catch (Exception e) {
                result.setStatus("fail");
                result.setReason(e.getMessage());
                allSuccess = false;
            }
            results.add(result);
        }
        response.setSuccess(allSuccess);
        response.setResults(results);
        return ResponseEntity.ok(response);
    }

    // 取消預約
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 狀態轉換
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Integer reservationId, @RequestBody ReservationDTO dto) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    reservation.setStatus(dto.getStatus());
                    reservation.setUpdatedAt(LocalDateTime.now());
                    ReservationEntity updated = reservationRepository.save(reservation);
                    return ResponseEntity.ok(reservationService.convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 