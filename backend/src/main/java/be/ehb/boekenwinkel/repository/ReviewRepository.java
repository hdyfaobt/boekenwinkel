package be.ehb.boekenwinkel.repository;

import be.ehb.boekenwinkel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access voor reviews.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** @return de reviews van een boek, nieuwste eerst */
    List<Review> findByBookIsbnOrderByCreatedAtDesc(String isbn);

    boolean existsByBookIsbnAndUserId(String isbn, Long userId);
}
