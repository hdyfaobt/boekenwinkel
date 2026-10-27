package be.ehb.boekenwinkel.controller;

import be.ehb.boekenwinkel.dto.LoginRequest;
import be.ehb.boekenwinkel.dto.LoginResponse;
import be.ehb.boekenwinkel.dto.ProfileDto;
import be.ehb.boekenwinkel.dto.RegisterRequest;
import be.ehb.boekenwinkel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-endpoints voor registreren, inloggen en het profiel.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** {@code POST /api/users/register} - nieuw account. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
    }

    /** {@code POST /api/users/login} - geeft een token terug. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /** {@code GET /api/users/me} - profiel met reservaties en gekochte boeken. */
    @GetMapping("/me")
    public ProfileDto profile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return userService.getProfile(userService.getLoggedInUser(authorization));
    }
}
