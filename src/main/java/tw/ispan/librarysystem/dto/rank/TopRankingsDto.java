package tw.ispan.librarysystem.dto.rank;

import java.util.List;

public class TopRankingsDto {
    private List<TopRankingsBookDto> reservationRanking;
    private List<TopRankingsBookDto> borrowRanking;
    private List<TopRankingsBookDto> ratingRanking;

    public TopRankingsDto(List<TopRankingsBookDto> reservationRanking,
                          List<TopRankingsBookDto> borrowRanking,
                          List<TopRankingsBookDto> ratingRanking) {
        this.reservationRanking = reservationRanking;
        this.borrowRanking = borrowRanking;
        this.ratingRanking = ratingRanking;
    }

    public List<TopRankingsBookDto> getReservationRanking() { return reservationRanking; }
    public void setReservationRanking(List<TopRankingsBookDto> reservationRanking) { this.reservationRanking = reservationRanking; }

    public List<TopRankingsBookDto> getBorrowRanking() { return borrowRanking; }
    public void setBorrowRanking(List<TopRankingsBookDto> borrowRanking) { this.borrowRanking = borrowRanking; }

    public List<TopRankingsBookDto> getRatingRanking() { return ratingRanking; }
    public void setRatingRanking(List<TopRankingsBookDto> ratingRanking) { this.ratingRanking = ratingRanking; }
}
