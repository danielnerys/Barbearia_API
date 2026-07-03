package com.danielnery.barbearia.api.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class JwtService {
    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(Authentication authentication) {
        try {
            UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.create().withIssuer("barbearia-api")
                    .withSubject(userPrincipal.getUsername())
                    .withExpiresAt(gerenateExpirationDate())
                    .withClaim("roles", userPrincipal.getAuthorities().stream().map(auth -> auth.getAuthority()).toList()).sign(algorithm);
        }catch (Exception exception){
            throw  new RuntimeException("Erro ao gerar token JWT", exception);
        }

    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer("barbearia-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant gerenateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }


}
