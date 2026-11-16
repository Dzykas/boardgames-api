package com.eli.boardgames.dto;

import com.eli.boardgames.model.Role;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        Instant createdAt
) {
}