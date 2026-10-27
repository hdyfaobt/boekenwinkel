package be.ehb.boekenwinkel.controller;

import be.ehb.boekenwinkel.dto.ReservationDto;
import be.ehb.boekenwinkel.dto.ReservationRequest;
import be.ehb.boekenwinkel.model.User;
import be.ehb.boekenwinkel.service.ReservationService;
import be.ehb.boekenwinkel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-endpoint om boeken te reserveren.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    /** {@code POST /api/reservations} - reserveren, geeft de bevestiging met afhaaldatum terug. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto reserve(@RequestHeader(value = "Authorization", required = false) String authorization,
                                  @Valid @RequestBody ReservationRequest request) {
        User user = userService.getLoggedInUser(authorization);
        return reservationService.reserve(user, request);
    }
}
