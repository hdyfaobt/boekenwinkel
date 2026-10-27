package be.ehb.boekenwinkel.service;

import be.ehb.boekenwinkel.dto.ReviewDto;
import be.ehb.boekenwinkel.dto.ReviewRequest;
import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.model.Review;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.repository.BookRepository;
import be.ehb.boekenwinkel.repository.ReservationRepository;
import be.ehb.boekenwinkel.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Reviews schrijven. Een gebruiker mag enkel een review geven op een boek dat hij gekocht heeft,
 * en maar één keer per boek.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, ReservationRepository reservationRepository,
                         BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Slaat een review op.
     *
     * @param user    de ingelogde gebruiker
     * @param isbn    het boek
     * @param request score en tekst
     * @return de opgeslagen review
     * @throws ResponseStatusException 404 als het boek niet bestaat, 403 als de gebruiker het boek niet
     *                                 gekocht heeft, 409 als hij al een review schreef
     */
    @Transactional
    public ReviewDto writeReview(User user, String isbn, ReviewRequest request) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boek " + isbn + " bestaat niet"));
        if (!reservationRepository.existsByUserIdAndItemsBookIsbn(user.getId(), isbn)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Je kan enkel een review schrijven over een boek dat je gekocht hebt");
        }
        if (reviewRepository.existsByBookIsbnAndUserId(isbn, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Je hebt al een review geschreven voor dit boek");
        }
        Review review = new Review(book, user, request.score(), request.text(), LocalDateTime.now());
        return ReviewDto.from(reviewRepository.save(review));
    }
}
