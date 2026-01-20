package com.example.Blogbackend.post;

import java.time.Instant;

public record Post(
        long id,
        long authorId,
        String title,
        String slug,
        String content,
        PostStatus status,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
