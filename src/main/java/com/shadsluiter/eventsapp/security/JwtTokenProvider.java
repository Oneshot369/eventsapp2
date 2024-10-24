package com.shadsluiter.eventsapp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

// This class is responsible for generating and validating JWT tokens.
@Component
public class JwtTokenProvider {

    // get the secret key and expiration time from the application.properties file
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs; 

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    // generate a token for the user
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // create the token with the username, the current date, the expiration date, and the secret key
        // builder chain pattern used to create the token
         String token = Jwts.builder()
                .subject(username) // the username is the subject of the token. It will be extracted from the token when the user sends a request to the server.
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();

                // Jwts.SIG.HS512 is the algorithm used to sign the token.
                // 512 means the key is 512 bits long.
                // The longer the key, the more secure the token.
                // SIG is short for signature.
                // The signature is used to verify that the token has not been tampered with.
                // other algorithms could be RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512

        return token;
    }

    // given a token, extract the username
    // used when the user sends a request to the server attempting to access a protected resource
    public String getUsernameFromJWT(String token) {
        // claims is the payload of the token which contains the username in hashed form
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username  =  claims.getSubject();  // fetch the username from the claims (payload) of the token
        return username;
    }

    // the server can validate because it has the secret key loaded from the application.properties file
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception ex) {
            // Handle exceptions
        }
        return false;
    }
}
