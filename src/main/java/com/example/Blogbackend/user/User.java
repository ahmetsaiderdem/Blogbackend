package com.example.Blogbackend.user;

import java.time.Instant;

public record User(

        Long id,
        String email,
        String passwordHash,
        String displayName,
        String role,
        Instant createdAt
) {
}
