package com.example.Blogbackend.comment.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;

public class CommentDtos {

    public record CreateCommentRequest(
            @NotBlank String content
    ){}

    public record CommentResponse(
            long id,
            long userId,
            String userDisplayName,
            String content,
            Instant createdAt
    ){}

    public record PageMeta(int page,int size,long total){}
    public record PageResponse<T>(List<T> items, PageMeta meta){}
}
