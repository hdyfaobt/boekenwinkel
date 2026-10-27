package be.ehb.boekenwinkel.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests voor de stockregels in {@link Book}.
 */
class BookTest {

    private Book book() {
        return new Book("9780132350884", "Clean Code", List.of("Robert C. Martin"), "", new BigDecimal("42.50"), 5);
    }

    @Test
    void removeStockVerlaagtDeStock() {
        Book book = book();
        book.removeStock(2);
        assertEquals(3, book.getStock());
    }

    @Test
    void laatsteExemplaarKanGereserveerdWorden() {
        Book book = book();
        book.removeStock(5);
        assertEquals(0, book.getStock());
    }

    @Test
    void meerDanDeStockGeeftFoutEnStockBlijftGelijk() {
        Book book = book();
        assertThrows(IllegalArgumentException.class, () -> book.removeStock(6));
        assertEquals(5, book.getStock());
    }

    @Test
    void nulOfNegatiefAantalIsOngeldig() {
        Book book = book();
        assertThrows(IllegalArgumentException.class, () -> book.removeStock(0));
        assertThrows(IllegalArgumentException.class, () -> book.addStock(-1));
    }

    @Test
    void leveringVerhoogtDeStock() {
        Book book = book();
        book.addStock(10);
        assertEquals(15, book.getStock());
    }
}
