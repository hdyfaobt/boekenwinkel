package be.ehb.boekenwinkel.service;

import be.ehb.boekenwinkel.dto.ReservationDto;
import be.ehb.boekenwinkel.dto.ReservationRequest;
import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.model.Reservation;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.repository.BookRepository;
import be.ehb.boekenwinkel.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Boeken reserveren. Bij een reservatie gaat de stock in de catalogus meteen omlaag
 * en krijgt de gebruiker een afhaaldatum als bevestiging.
 */
@Service
public class ReservationService {

    /** Aantal dagen tussen de reservatie en het moment dat de boeken klaarliggen. */
    public static final int DAYS_UNTIL_PICKUP = 1;

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Maakt een reservatie aan en haalt de boeken uit de stock.
     * De methode is transactioneel: lukt één boek niet (bv. te weinig stock),
     * dan wordt niets aangepast.
     *
     * @param user    de ingelogde gebruiker
     * @param request de boeken en aantallen
     * @return de bevestiging met afhaaldatum
     * @throws ResponseStatusException 404 als een boek niet bestaat, 409 als er te weinig stock is
     */
    @Transactional
    public ReservationDto reserve(User user, ReservationRequest request) {
        LocalDate pickupDate = LocalDate.now().plusDays(DAYS_UNTIL_PICKUP);
        Reservation reservation = new Reservation(user, LocalDateTime.now(), pickupDate);

        for (ReservationRequest.Line line : request.items()) {
            Book book = bookRepository.findById(line.isbn())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boek " + line.isbn() + " bestaat niet"));
            try {
                book.removeStock(line.quantity());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            bookRepository.save(book);
            reservation.addItem(book, line.quantity());
        }

        return ReservationDto.from(reservationRepository.save(reservation));
    }
}
