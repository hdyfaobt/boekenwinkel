package be.ehb.boekenwinkel.repository;

import be.ehb.boekenwinkel.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access voor boeken. Spring Data genereert de implementatie.
 */
public interface BookRepository extends JpaRepository<Book, String> {

    /** @return alle boeken, alfabetisch op titel */
    List<Book> findAllByOrderByTitleAsc();
}
