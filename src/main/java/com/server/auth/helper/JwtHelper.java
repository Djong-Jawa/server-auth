package com.server.auth.helper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtHelper {

//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String SECRET_KEY = "hello";
    private static final int MINUTES = 60;

    public static String generateToken(String email, PrivateKey privateKey) {
        var now = Instant.now();

        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(privateKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractUsername(String token, PublicKey publicKey) throws AccessDeniedException {
        return getTokenBody(token, publicKey).getSubject();
    }

    public static Boolean validateToken(String token, UserDetails userDetails, PublicKey publicKey) throws AccessDeniedException {
        final String username = extractUsername(token, publicKey);
        return username.equals(userDetails.getUsername()) && !isTokenExpire(token, publicKey);
    }

    public static Claims getTokenBody(String token, PublicKey publicKey) throws AccessDeniedException {
        try{
            System.out.println("token in get token body...");
            System.out.println(token);
            // Using parserBuilder instead of parser and parseClaimsJws for full JWT
            return Jwts
                    .parserBuilder()
                    .setSigningKey(publicKey) // Use PublicKey to verify RS256 signature
                    .build()
                    .parseClaimsJws(token)   // Changed to parseClaimsJws for signed JWT
                    .getBody();

        } catch (ExpiredJwtException e) {
            System.out.println("error get token body ...");
            System.out.println(e.getMessage());
            throw new AccessDeniedException("Access denied "+e.getMessage());
        }
    }

    public static boolean isTokenExpire(String token, PublicKey publicKey) throws AccessDeniedException {
        Claims claims = getTokenBody(token, publicKey);
        return claims.getExpiration().before(new Date());
    }


}
