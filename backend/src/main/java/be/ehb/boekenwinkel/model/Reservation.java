package be.ehb.boekenwinkel.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Een reservatie van een gebruiker. Ze bevat een of meer boeken ({@link ReservationItem})
 * en de datum vanaf wanneer de gebruiker de boeken mag afhalen en betalen.
 * We gaan ervan uit dat alles wat gereserveerd is ook afgehaald wordt, dus een
 * gereserveerd boek telt als gekocht.
 */
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    private LocalDateTime createdAt;

    private LocalDate pickupDate;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationItem> items = new ArrayList<>();

    protected Reservation() {
        // nodig voor JPA
    }

    public Reservation(User user, LocalDateTime createdAt, LocalDate pickupDate) {
        this.user = user;
        this.createdAt = createdAt;
        this.pickupDate = pickupDate;
    }

    /**
     * Voegt een boek toe aan deze reservatie.
     *
     * @param book     het gereserveerde boek
     * @param quantity aantal exemplaren
     */
    public void addItem(Book book, int quantity) {
        items.add(new ReservationItem(this, book, quantity));
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public List<ReservationItem> getItems() {
        return items;
    }
}
