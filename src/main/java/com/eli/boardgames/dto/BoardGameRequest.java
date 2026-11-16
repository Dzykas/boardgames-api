package com.eli.boardgames.dto;

import com.eli.boardgames.model.Complexity;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BoardGameRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 100) String publisher,
        @NotNull @Min(1) @Max(20) Integer minPlayers,
        @NotNull @Min(1) @Max(20) Integer maxPlayers,
        @NotNull @Min(5) @Max(60000) Integer playTimeMinutes,
        @NotNull Complexity complexity,
        @PastOrPresent LocalDate purchaseDate
) {
    @AssertTrue(message = "maxPlayers must be at least minPlayers")
    public boolean isValidPlayerRange() {
        if (minPlayers == null || maxPlayers == null) {
            return true;
        }
        return minPlayers <= maxPlayers;
    }
}