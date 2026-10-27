package be.ehb.boekenwinkel.controller;

import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.repository.BookRepository;
import be.ehb.boekenwinkel.repository.ReservationRepository;
import be.ehb.boekenwinkel.repository.ReviewRepository;
import be.ehb.boekenwinkel.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Functionele test: de hele applicatie draait met een H2-databank in het geheugen
 * en we doorlopen de flow zoals een gebruiker in de frontend.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BoekenwinkelApiTest {

    private static final String ISBN = "9780132350884";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();
        bookRepository.save(new Book(ISBN, "Clean Code", List.of("Robert C. Martin"), "", new BigDecimal("42.50"), 5));
    }

    /** Registreert een gebruiker, logt in en geeft de Authorization-header terug. */
    private String registerAndLogin(String username) throws Exception {
        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","email":"%s@test.be","password":"geheim123"}""".formatted(username, username)))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"geheim123"}""".formatted(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }

    @Test
    void reserverenPastCatalogusEnProfielAan() throws Exception {
        String auth = registerAndLogin("houde");

        mockMvc.perform(post("/api/reservations").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"isbn":"%s","quantity":2}]}""".formatted(ISBN)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickupDate").exists());

        // de catalogus toont minder stock
        mockMvc.perform(get("/api/books/" + ISBN))
                .andExpect(jsonPath("$.book.stock").value(3));

        // het boek staat bij de gekochte boeken in het profiel
        mockMvc.perform(get("/api/users/me").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchasedBooks[0].isbn").value(ISBN))
                .andExpect(jsonPath("$.reservations.length()").value(1));
    }

    @Test
    void reviewVerschijntOpDeDetailpagina() throws Exception {
        String auth = registerAndLogin("houde");
        mockMvc.perform(post("/api/reservations").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"isbn":"%s","quantity":1}]}""".formatted(ISBN)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books/" + ISBN + "/reviews").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score":4,"text":"Goed boek"}"""))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/" + ISBN))
                .andExpect(jsonPath("$.reviews[0].username").value("houde"))
                .andExpect(jsonPath("$.reviews[0].score").value(4))
                .andExpect(jsonPath("$.averageScore").value(4.0));
    }

    @Test
    void reviewZonderAankoopWordtGeweigerd() throws Exception {
        String auth = registerAndLogin("houde");

        mockMvc.perform(post("/api/books/" + ISBN + "/reviews").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score":5,"text":"Nooit gelezen"}"""))
                .andExpect(status().isForbidden());
    }

    @Test
    void reserverenZonderLoginGeeft401() throws Exception {
        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"isbn":"%s","quantity":1}]}""".formatted(ISBN)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void foutWachtwoordGeeft401() throws Exception {
        registerAndLogin("houde");

        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"houde","password":"fout"}"""))
                .andExpect(status().isUnauthorized());
    }
}
