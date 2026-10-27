package be.ehb.boekenwinkel.controller;

import be.ehb.boekenwinkel.dto.BookDetailDto;
import be.ehb.boekenwinkel.dto.BookDto;
import be.ehb.boekenwinkel.dto.ReviewDto;
import be.ehb.boekenwinkel.dto.ReviewRequest;
import be.ehb.boekenwinkel.dto.StockRequest;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.service.BookService;
import be.ehb.boekenwinkel.service.ReviewService;
import be.ehb.boekenwinkel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-endpoints van de catalogus.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService;

    public BookController(BookService bookService, ReviewService reviewService, UserService userService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    /** {@code GET /api/books} - alle boeken met prijs en stock. */
    @GetMapping
    public List<BookDto> getAll() {
        return bookService.getAllBooks();
    }

    /** {@code GET /api/books/{isbn}} - één boek met zijn reviews. */
    @GetMapping("/{isbn}")
    public BookDetailDto getOne(@PathVariable String isbn) {
        return bookService.getBookDetail(isbn);
    }

    /** {@code POST /api/books/{isbn}/stock} - stock aanvullen als er een levering binnenkomt. */
    @PostMapping("/{isbn}/stock")
    public BookDto addStock(@PathVariable String isbn, @Valid @RequestBody StockRequest request) {
        return bookService.addStock(isbn, request.quantity());
    }

    /** {@code POST /api/books/{isbn}/reviews} - review schrijven (ingelogd, boek gekocht). */
    @PostMapping("/{isbn}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto writeReview(@RequestHeader(value = "Authorization", required = false) String authorization,
                                 @PathVariable String isbn,
                                 @Valid @RequestBody ReviewRequest request) {
        User user = userService.getLoggedInUser(authorization);
        return reviewService.writeReview(user, isbn, request);
    }
}
