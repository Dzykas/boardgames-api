package com.eli.boardgames.dto;

import com.eli.boardgames.model.Complexity;

import java.time.Instant;
import java.time.LocalDate;

public record BoardGameResponse(
        Long id,

        String title,
        String publisher,
        Integer minPlayers,
        Integer maxPlayers,
        Integer playTimeMinutes,
        Complexity complexity,
        LocalDate purchaseDate,

        Instant createdAt,
        Instant updatedAt
) {
}
