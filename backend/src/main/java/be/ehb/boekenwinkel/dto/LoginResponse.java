package be.ehb.boekenwinkel.dto;

/**
 * Antwoord na het inloggen. De frontend stuurt het token mee in de header
 * {@code Authorization: Bearer <token>}.
 */
public record LoginResponse(String token, String username) {
}
