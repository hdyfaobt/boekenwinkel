package be.ehb.boekenwinkel.service;

import be.ehb.boekenwinkel.dto.BookDetailDto;
import be.ehb.boekenwinkel.dto.BookDto;
import be.ehb.boekenwinkel.dto.ReviewDto;
import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.repository.BookRepository;
import be.ehb.boekenwinkel.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Logica rond de catalogus: boeken opvragen en stock aanvullen.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * @return alle boeken uit de catalogus, gesorteerd op titel
     */
    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks() {
        return bookRepository.findAllByOrderByTitleAsc().stream().map(BookDto::from).toList();
    }

    /**
     * Geeft een boek terug met zijn reviews en gemiddelde score.
     *
     * @param isbn ISBN van het boek
     * @return de gegevens voor de detailpagina
     * @throws ResponseStatusException 404 als het boek niet bestaat
     */
    @Transactional(readOnly = true)
    public BookDetailDto getBookDetail(String isbn) {
        Book book = findBook(isbn);
        List<ReviewDto> reviews = reviewRepository.findByBookIsbnOrderByCreatedAtDesc(isbn).stream()
                .map(ReviewDto::from)
                .toList();
        Double average = reviews.isEmpty() ? null
                : reviews.stream().mapToInt(ReviewDto::score).average().orElse(0);
        return new BookDetailDto(BookDto.from(book), average, reviews);
    }

    /**
     * Vult de stock van een boek aan, bijvoorbeeld als er een levering binnenkomt.
     *
     * @param isbn     ISBN van het boek
     * @param quantity aantal nieuwe exemplaren
     * @return het boek met de nieuwe stock
     */
    @Transactional
    public BookDto addStock(String isbn, int quantity) {
        Book book = findBook(isbn);
        book.addStock(quantity);
        return BookDto.from(bookRepository.save(book));
    }

    /**
     * Zoekt een boek op ISBN.
     *
     * @throws ResponseStatusException 404 als het boek niet bestaat
     */
    public Book findBook(String isbn) {
        return bookRepository.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boek " + isbn + " bestaat niet"));
    }
}
