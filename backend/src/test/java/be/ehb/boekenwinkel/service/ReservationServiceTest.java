package be.ehb.boekenwinkel.service;

import be.ehb.boekenwinkel.dto.ReservationDto;
import be.ehb.boekenwinkel.dto.ReservationRequest;
import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.model.Reservation;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.repository.BookRepository;
import be.ehb.boekenwinkel.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests voor {@link ReservationService}. De repositories zijn mocks,
 * zo testen we enkel de logica en niet de databank.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BookRepository bookRepository;

    private ReservationService service;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationRepository, bookRepository);
        user = new User("houde", "houde@test.be", "hash");
        book = new Book("9780747532699", "Harry Potter", List.of("JK Rowling"),
                "Harry Potter is een reeks fantasyromans geschreven door de Britse schrijfster J.K. Rowling.", new BigDecimal("42.50"), 5);
    }

    @Test
    void reservatieVerlaagtDeStockEnGeeftAfhaaldatum() {
        when(bookRepository.findById("9780747532699")).thenReturn(Optional.of(book));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(call -> call.getArgument(0));

        ReservationDto result = service.reserve(user,
                new ReservationRequest(List.of(new ReservationRequest.Line("9780747532699", 2))));

        assertEquals(3, book.getStock());
        assertEquals(LocalDate.now().plusDays(ReservationService.DAYS_UNTIL_PICKUP), result.pickupDate());
        assertEquals(1, result.items().size());
        assertEquals(2, result.items().get(0).quantity());
        verify(bookRepository).save(book);
    }

    @Test
    void teWeinigStockGeeft409EnSlaatNietsOp() {
        when(bookRepository.findById("9780747532699")).thenReturn(Optional.of(book));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.reserve(user,
                new ReservationRequest(List.of(new ReservationRequest.Line("9780747532699", 6)))));

        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        assertEquals(5, book.getStock());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void onbestaandBoekGeeft404() {
        when(bookRepository.findById("0000000000000")).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.reserve(user,
                new ReservationRequest(List.of(new ReservationRequest.Line("0000000000000", 1)))));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }
}
