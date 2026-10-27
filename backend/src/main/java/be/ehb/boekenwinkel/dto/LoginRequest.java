package be.ehb.boekenwinkel.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body om in te loggen.
 */
public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}
