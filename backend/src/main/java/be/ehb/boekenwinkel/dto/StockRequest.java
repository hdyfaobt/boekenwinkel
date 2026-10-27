package be.ehb.boekenwinkel.dto;

import jakarta.validation.constraints.Min;

/**
 * Body om stock toe te voegen wanneer er een levering binnenkomt.
 */
public record StockRequest(@Min(1) int quantity) {
}
