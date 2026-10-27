package be.ehb.boekenwinkel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body om een review te schrijven.
 */
public record ReviewRequest(
        @Min(1) @Max(5) int score,
        @NotBlank @Size(max = 2000) String text) {
}
