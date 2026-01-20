package com.example.Blogbackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String displayName
    ){}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ){}

    public record TokenResponse(String accessToken){}
}
