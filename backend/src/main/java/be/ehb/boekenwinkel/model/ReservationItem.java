package be.ehb.boekenwinkel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Eén lijn van een reservatie: welk boek en hoeveel exemplaren.
 */
@Entity
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Reservation reservation;

    @ManyToOne(optional = false)
    private Book book;

    private int quantity;

    protected ReservationItem() {
        // nodig voor JPA
    }

    public ReservationItem(Reservation reservation, Book book, int quantity) {
        this.reservation = reservation;
        this.book = book;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }
}
