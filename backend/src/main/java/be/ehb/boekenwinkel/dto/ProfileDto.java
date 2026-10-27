package be.ehb.boekenwinkel.dto;

import java.util.List;

/**
 * Profiel van de ingelogde gebruiker met zijn reservaties en de boeken die hij gekocht heeft.
 */
public record ProfileDto(String username, String email, List<ReservationDto> reservations,
                         List<BookDto> purchasedBooks) {
}
