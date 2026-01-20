package com.example.Blogbackend.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class PostDtos {

    public record CreatePostRequest(
            @NotBlank @Size(min = 3,max=250)String title,
            @NotBlank String Content
    ){}

    public record UpdatePostRequest(
            @NotBlank @Size(min = 3,max = 250)String title,
            @NotBlank String content
    ){}

    public record PostDetailResponse(
            long id,
            long authorId,
            String title,
            String slug,
            String content,
            String status,
            Instant publishedAt,
            Instant createdAt,
            Instant updatedAt
    ){}

    public record PostListItemResponse(
            long id,
            String title,
            String slug,
            Instant publishedAt
    ){}
    public record PageMeta(int page,int size,long total){}
    public record PageResponse<T>(List<T> items, PageMeta meta){}
}
