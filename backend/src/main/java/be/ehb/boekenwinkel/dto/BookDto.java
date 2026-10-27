package be.ehb.boekenwinkel.dto;

import be.ehb.boekenwinkel.model.Book;

import java.math.BigDecimal;
import java.util.List;

/**
 * Een boek zoals de API het teruggeeft in de catalogus.
 */
public record BookDto(String isbn, String title, List<String> authors, String description,
                      BigDecimal price, int stock) {

    public static BookDto from(Book book) {
        return new BookDto(book.getIsbn(), book.getTitle(), List.copyOf(book.getAuthors()),
                book.getDescription(), book.getPrice(), book.getStock());
    }
}
