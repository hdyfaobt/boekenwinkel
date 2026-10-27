package be.ehb.boekenwinkel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

/**
 * Een review van een gebruiker op een boek dat hij gekocht heeft: een score van 1 tot 5 en een tekst.
 */
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private User user;

    private int score;

    @Column(length = 2000)
    private String text;

    private LocalDateTime createdAt;

    protected Review() {
        // nodig voor JPA
    }

    public Review(Book book, User user, int score, String text, LocalDateTime createdAt) {
        this.book = book;
        this.user = user;
        this.score = score;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
