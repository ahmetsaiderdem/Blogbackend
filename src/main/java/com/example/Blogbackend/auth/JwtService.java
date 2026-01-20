package com.example.Blogbackend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final long minutes;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.issuer}")String issuer,
                      @Value("${app.jwt.access-token-minutes}")long minutes
    ){
        this.encoder=encoder;
        this.issuer=issuer;
        this.minutes=minutes;
    }

    public String createAccessToken(long userId,String email,String role){
        Instant now=Instant.now();
        Instant exp=now.plusSeconds(minutes *60);

        JwtClaimsSet claims=JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                .subject(email)
                .claim("uid",userId)
                .claim("roles",role)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }



}
