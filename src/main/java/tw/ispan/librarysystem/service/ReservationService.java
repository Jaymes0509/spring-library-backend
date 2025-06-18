// @SuppressWarnings("SpellCheckingInspection")
package tw.ispan.librarysystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.reservation.ReservationDTO;
import tw.ispan.librarysystem.entity.reservation.ReservationEntity;
import tw.ispan.librarysystem.repository.reservation.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<ReservationDTO> getAllReservationsWithBookInfo() {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReservationDTO> getReservationById(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::convertToDTO);
    }

    public List<ReservationDTO> getReservationsByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getReservationsByBookId(Integer bookId) {
        return reservationRepository.findByBookBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO convertToDTO(ReservationEntity reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setReserveTime(reservation.getReserveTime());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        // 設定取書相關資訊
        dto.setPickupLocation("圖書館一樓櫃檯"); // 這裡可以根據實際業務邏輯設定
        dto.setPickupTime(reservation.getReserveTime().plusDays(3)); // 假設預約後3天內取書

        if (reservation.getBook() != null) {
            dto.setBookId(reservation.getBook().getBookId());
            dto.setTitle(reservation.getBook().getTitle());
            dto.setIsbn(reservation.getBook().getIsbn());
            dto.setAuthor(reservation.getBook().getAuthor());
            dto.setPublisher(reservation.getBook().getPublisher());
            dto.setClassification(reservation.getBook().getClassification());
            
            if (reservation.getBook().getCategory() != null) {
                dto.setCategoryName(reservation.getBook().getCategory().getcName());
            }
        }

        return dto;
    }

    // 新增：建立預約（單本）
    public ReservationEntity createReservation(ReservationDTO dto) {
        ReservationEntity entity = new ReservationEntity();
        entity.setUserId(dto.getUserId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setReserveTime(dto.getReserveTime());
        entity.setExpiryDate(dto.getReserveTime() != null ? dto.getReserveTime().plusDays(3) : null);
        
        // 關聯書籍
        if (dto.getBookId() != null) {
            tw.ispan.librarysystem.entity.books.BookEntity book = new tw.ispan.librarysystem.entity.books.BookEntity();
            book.setBookId(dto.getBookId());
            entity.setBook(book);
        } else {
            throw new RuntimeException("bookId 不可為空");
        }
        
        return reservationRepository.save(entity);
    }
} 