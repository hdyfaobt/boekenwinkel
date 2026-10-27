package be.ehb.boekenwinkel.dto;

import java.util.List;

/**
 * Detailpagina van een boek: het boek zelf, de gemiddelde score en alle reviews.
 *
 * @param averageScore gemiddelde score, of null als er nog geen reviews zijn
 */
public record BookDetailDto(BookDto book, Double averageScore, List<ReviewDto> reviews) {
}
