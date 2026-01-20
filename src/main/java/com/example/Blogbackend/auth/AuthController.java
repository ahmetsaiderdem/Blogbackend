package com.example.Blogbackend.auth;


import com.example.Blogbackend.auth.dto.AuthDtos;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    public AuthService auth;

    public AuthController(AuthService auth){
        this.auth=auth;
    }

    @PostMapping("/register")
    public AuthDtos.TokenResponse register(@Valid @RequestBody AuthDtos.RegisterRequest req){
        return new AuthDtos.TokenResponse(auth.register(req));
    }

    @PostMapping("login")
    public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest req){
        return new AuthDtos.TokenResponse(auth.login(req));
    }

    @GetMapping("/me")
    public Object me(@AuthenticationPrincipal Jwt jwt){
        return new Object(){
            public final String email=jwt.getSubject();
            public final Object uid=jwt.getClaims().get("uid");
            public final Object roles=jwt.getClaims().get("roles");
        };
    }
}
