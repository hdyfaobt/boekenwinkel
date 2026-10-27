package be.ehb.boekenwinkel.dto;

import be.ehb.boekenwinkel.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Bevestiging van een reservatie. {@code pickupDate} is de dag vanaf wanneer
 * de gebruiker zijn boeken mag afhalen en betalen.
 */
public record ReservationDto(Long id, LocalDateTime createdAt, LocalDate pickupDate, List<Line> items) {

    /** Een gereserveerd boek. */
    public record Line(String isbn, String title, int quantity) {
    }

    public static ReservationDto from(Reservation reservation) {
        List<Line> lines = reservation.getItems().stream()
                .map(item -> new Line(item.getBook().getIsbn(), item.getBook().getTitle(), item.getQuantity()))
                .toList();
        return new ReservationDto(reservation.getId(), reservation.getCreatedAt(), reservation.getPickupDate(), lines);
    }
}
