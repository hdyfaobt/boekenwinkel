package be.ehb.boekenwinkel.repository;

import be.ehb.boekenwinkel.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access voor reservaties.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** @return de reservaties van een gebruiker, nieuwste eerst */
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** @return true als de gebruiker dit boek ooit gereserveerd (en dus gekocht) heeft */
    boolean existsByUserIdAndItemsBookIsbn(Long userId, String isbn);
}
