package be.ehb.boekenwinkel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startpunt van de backend. Start een REST API op poort 8080.
 */
@SpringBootApplication
public class BoekenwinkelApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoekenwinkelApplication.class, args);
    }
}
