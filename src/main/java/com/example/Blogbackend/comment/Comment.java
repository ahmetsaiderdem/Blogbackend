package com.example.Blogbackend.comment;

import java.time.Instant;

public record Comment(
        long id,
        long postId,
        long userId,
        String content,
        Instant createdAt
) {
}
