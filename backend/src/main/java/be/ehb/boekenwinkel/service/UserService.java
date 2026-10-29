package be.ehb.boekenwinkel.service;

import be.ehb.boekenwinkel.dto.BookDto;
import be.ehb.boekenwinkel.dto.LoginRequest;
import be.ehb.boekenwinkel.dto.LoginResponse;
import be.ehb.boekenwinkel.dto.ProfileDto;
import be.ehb.boekenwinkel.dto.RegisterRequest;
import be.ehb.boekenwinkel.dto.ReservationDto;
import be.ehb.boekenwinkel.model.Book;
import be.ehb.boekenwinkel.model.Reservation;
import be.ehb.boekenwinkel.model.ReservationItem;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.repository.ReservationRepository;
import be.ehb.boekenwinkel.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Registreren, inloggen en het profiel van een gebruiker.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserRepository userRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Maakt een nieuw account aan.
     *
     * @param request gebruikersnaam, e-mail en wachtwoord
     * @throws ResponseStatusException 409 als de gebruikersnaam of het e-mailadres al bestaat
     */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deze gebruikersnaam is al in gebruik");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dit e-mailadres is al in gebruik");
        }
        userRepository.save(new User(request.username(), request.email(), hashPassword(request.password())));
    }

    /**
     * Controleert gebruikersnaam en wachtwoord en geeft een nieuw token terug.
     *
     * @throws ResponseStatusException 401 bij een foute combinatie
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(u -> checkPassword(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Foute gebruikersnaam of wachtwoord"));
        user.setToken(UUID.randomUUID().toString());
        userRepository.save(user);
        return new LoginResponse(user.getToken(), user.getUsername());
    }

    /**
     * Zoekt de ingelogde gebruiker aan de hand van de Authorization-header.
     *
     * @param authorization waarde van de header, bv. {@code Bearer 1234-abcd}
     * @return de gebruiker die bij het token hoort
     * @throws ResponseStatusException 401 als het token ontbreekt of ongeldig is
     */
    public User getLoggedInUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Je moet ingelogd zijn");
        }
        String token = authorization.substring("Bearer ".length());
        return userRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Je moet ingelogd zijn"));
    }

    /**
     * Stelt het profiel samen: gegevens, reservaties en gekochte boeken.
     * Elk gereserveerd boek telt als gekocht (zie opgave).
     *
     * @param user de ingelogde gebruiker
     */
    @Transactional(readOnly = true)
    public ProfileDto getProfile(User user) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // elk boek maar één keer in de lijst, ook al werd het meerdere keren gereserveerd
        Map<String, Book> purchased = new LinkedHashMap<>();
        for (Reservation reservation : reservations) {
            for (ReservationItem item : reservation.getItems()) {
                purchased.putIfAbsent(item.getBook().getIsbn(), item.getBook());
            }
        }

        return new ProfileDto(user.getUsername(), user.getEmail(),
                reservations.stream().map(ReservationDto::from).toList(),
                purchased.values().stream().map(BookDto::from).toList());
    }

    /**
     * Hasht een wachtwoord met SHA-256 en een willekeurige salt.
     * Opgeslagen als {@code salt:hash}, zodat twee gelijke wachtwoorden toch een andere hash krijgen.
     */
    private String hashPassword(String password) {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String salt = HexFormat.of().formatHex(saltBytes);
        return salt + ":" + sha256(salt + password);
    }











    private boolean checkPassword(String password, String stored) {
        String[] parts = stored.split(":");
        return parts.length == 2 && MessageDigest.isEqual(
                parts[1].getBytes(StandardCharsets.UTF_8),
                sha256(parts[0] + password).getBytes(StandardCharsets.UTF_8));
    }

    private String sha256(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
