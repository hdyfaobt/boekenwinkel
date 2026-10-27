package be.ehb.boekenwinkel.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Een boek uit de catalogus. Het ISBN is uniek en dient daarom als primary key.
 * De stock zit mee in deze entiteit, zodat de catalogus altijd de actuele stock toont.
 */
@Entity
public class Book {

    @Id
    @Column(length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    /** Een boek kan meerdere auteurs hebben (aparte tabel book_authors). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "isbn"))
    @OrderColumn(name = "author_order")
    @Column(name = "author")
    private List<String> authors = new ArrayList<>();

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private int stock;

    protected Book() {
        // nodig voor JPA
    }

    public Book(String isbn, String title, List<String> authors, String description, BigDecimal price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Haalt exemplaren uit de stock, bijvoorbeeld bij een reservatie.
     *
     * @param amount aantal exemplaren (groter dan 0)
     * @throws IllegalArgumentException als het aantal ongeldig is of er niet genoeg stock is
     */
    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Aantal moet groter dan 0 zijn");
        }
        if (amount > stock) {
            throw new IllegalArgumentException("Niet genoeg stock voor " + title);
        }
        stock -= amount;
    }

    /**
     * Voegt exemplaren toe aan de stock, bijvoorbeeld als er een levering binnenkomt.
     *
     * @param amount aantal exemplaren (groter dan 0)
     * @throws IllegalArgumentException als het aantal ongeldig is
     */
    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Aantal moet groter dan 0 zijn");
        }
        stock += amount;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
