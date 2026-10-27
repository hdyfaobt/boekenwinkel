package be.ehb.boekenwinkel.dto;

import be.ehb.boekenwinkel.model.Review;

import java.time.LocalDateTime;

/**
 * Een review zoals ze op de detailpagina van een boek getoond wordt.
 */
public record ReviewDto(String username, int score, String text, LocalDateTime createdAt) {

    public static ReviewDto from(Review review) {
        return new ReviewDto(review.getUser().getUsername(), review.getScore(),
                review.getText(), review.getCreatedAt());
    }
}
