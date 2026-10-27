package be.ehb.boekenwinkel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Body om boeken te reserveren.
 */
public record ReservationRequest(@NotEmpty List<@Valid Line> items) {

    /** Een boek en het aantal exemplaren. */
    public record Line(@NotBlank String isbn, @Min(1) int quantity) {
    }
}
