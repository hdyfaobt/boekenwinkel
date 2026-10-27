package be.ehb.boekenwinkel.config;

import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Zet een paar boeken in de databank bij de eerste opstart, zodat de catalogus niet leeg is.
 * Staat aan via {@code app.seed=true} in application.properties (in de tests staat het uit).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final boolean enabled;

    public DataSeeder(BookRepository bookRepository, @Value("${app.seed:false}") boolean enabled) {
        this.bookRepository = bookRepository;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) {
        if (!enabled || bookRepository.count() > 0) {
            return;
        }
        bookRepository.saveAll(List.of(
                new Book("9780132350884", "Harry Potter", List.of("JK Rowling"),
                        "Harry Potter is een reeks fantasyromans geschreven door de Britse schrijfster J.K. Rowling.", new BigDecimal("42.50"), 5),
                new Book("9780134685991", "Het leven van een loser", List.of("Jeff Kinney"),
                        "Het leven van een loser is een populaire kinderboekenreeks geschreven door de Amerikaanse auteur Jeff Kinney.", new BigDecimal("39.99"), 10),
                new Book("9780134685992", "Geronimo Stilton", List.of("Elisabetta Dami"),
                        "Geronimo Stilton is een populaire kinderboekenreeks geschreven door de Italiaanse auteur Elisabetta Dami onder het pseudoniem Geronimo Stilton.", new BigDecimal("29.99"), 7),
                new Book("9780134685993", "Naar de maan en terug", List.of("Roald Dahl"),
                        "Naar de maan en terug is een kinderboek geschreven door de Britse auteur Roald Dahl.", new BigDecimal("24.99"), 3),
                new Book("9780134685994", "De GVR", List.of("Roald Dahl"),
                        "De GVR is een kinderboek geschreven door de Britse auteur Roald Dahl.", new BigDecimal("19.99"), 8),
                new Book("9780134685997", "miss peregrine's home for peculiar children", List.of("Ransom Riggs"),
                        "Miss Peregrine's Home for Peculiar Children is een fantasyroman geschreven door de Amerikaanse auteur Ransom Riggs.", new BigDecimal("34.99"), 6),
                new Book("9780134685998", "Mathilda", List.of("Roald Dahl"),
                        "Matilda is een kinderboek geschreven door de Britse auteur Roald Dahl.", new BigDecimal("22.99"), 4),
                new Book("9780134685999", "De Hobbit", List.of("J.R.R. Tolkien"),
                        "De Hobbit is een fantasyroman geschreven door de Britse auteur J.R.R. Tolkien.", new BigDecimal("27.99"), 9),
                new Book("9780134685995", "De Kronieken van Narnia", List.of("C.S. Lewis"),
                        "De Kronieken van Narnia is een fantasyboekenreeks geschreven door de Britse auteur C.S. Lewis.", new BigDecimal("31.99"), 2)
        ));
    }
}
