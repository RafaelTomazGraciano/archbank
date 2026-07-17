package com.graciano.archbank.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class JwtTokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final String ISSUER = "ArchBank";
    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(30);

    private Algorithm hmac256;
    private JWTVerifier verifier;

    @PostConstruct
    public void init(){
        this.hmac256 = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(this.hmac256).withIssuer(ISSUER).build();
    }

    public String generateToken(UserDetails userDetails){
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(ISSUER)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(JWT_TOKEN_VALIDITY))
                .sign(hmac256);
    }

    public DecodedJWT validateToken(String token){
        try{
            return verifier.verify(token);
        }catch (JWTVerificationException ex){
            log.warn("token invalid: {}", ex.getMessage());
            return null;
        }
    }

    public String extractEmail(DecodedJWT jwt) {
        return jwt.getSubject();
    }

}
