package com.example.Blogbackend.auth;

import com.example.Blogbackend.auth.dto.AuthDtos;
import com.example.Blogbackend.user.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    public final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt){
        this.users=users;
        this.encoder=encoder;
        this.jwt=jwt;
    }

    public String register(AuthDtos.RegisterRequest req){
        String hash=encoder.encode(req.password());
        try {
            long id=users.insert(req.email(),hash, req.displayName(),"AUTHOR");
            return jwt.createAccessToken(id,req.email(),"AUTHOR");
        }catch (DuplicateKeyException e){
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }
    }

    public String login(AuthDtos.LoginRequest req){
        var user=users.findByEmail(req.email()).orElseThrow(()-> new RuntimeException("INVALID_CREDENTIALS"));
        if (!encoder.matches(req.password(), user.passwordHash())){
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        return jwt.createAccessToken(user.id(), user.email(),user.role());
    }


}
