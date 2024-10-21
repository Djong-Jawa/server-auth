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

    public static String generateToken(String email, String privateKey) {
        var now = Instant.now();

        // Generate a SecretKey for HS256 algorithm
        SecretKey key = new SecretKeySpec(privateKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());

        // Generate JWT using HS256 with the secret key
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)  // Use HS256 for HMAC
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
            String privateKeyPEMString = "-----BEGIN PRIVATE KEY-----\n" +
                    "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC4DDYubNQUjBV6\n" +
                    "8b5S80ELdhUMTicq0BrzwpXIQ5Sygkxy9fFN6B10J68AlNNaWfwggCuoYWW2PV4s\n" +
                    "je3W2H1gGQrf0KCiy7sYzjhR+IUyXgWu1awbdAh1P9KsSAxWTFvsOFEgoafBldyB\n" +
                    "8AuCw7QeL9OVRETHfoTy1ayjEjIV9/cgxvLt2xwcnjP/nV31OZN0HMhcksVDantl\n" +
                    "X2I4gTlR2t8Pg63KpeIcKrAAeKc/4f8d9qIlqBYL+H2sDP/pdeHyFOi4yJdnO+TY\n" +
                    "4EraIlWRwZOKhYrQ9AZsJkPerfyg+1Yyv+dPVX7PRc4cNfaVdkDexbEv0BHphgGx\n" +
                    "5DzteQBFAgMBAAECggEAEJk1V7mD5Ph/RW/A6O08TALRwiLYQHG1RI+jf4Ssx+6d\n" +
                    "Y27P3OVWpq+fhk9lwkvEQ9AbX9us1yMwt5bzQMGUiRKpGudPXW8/R+ztQmNF6/Vz\n" +
                    "0ME/fNhZnO1tHBGBX1USozqvAmDh5sETunpCJ4Br4Z8Rl3ApPc6owdfIcoqFDtr/\n" +
                    "JUHfN+e7sVIMSMZvTWj9tjd/E3nf3IieCJoxq3a5Qjt/q+i19t8DRbZcx0N9a4N/\n" +
                    "/y6qjzIEsp6kY1XNVITE99vfhmBh+nJ8juxOB2hmtO+JAUoI9iFeU8cOahGgslBe\n" +
                    "LPMuolmHPPqQwNm9mW8YVfouB8wu9fjK/BpA5DHsgQKBgQD3xWmJ3Bd5QCROVFYt\n" +
                    "xUfpn8zrgo0Nttjm26DQRQWYLFBg21pMLzuNp6YvMO5JcIL12uQ5OMarr4DnihH2\n" +
                    "qkVp7Cd8URc2IY5ffGrbLuCGnqiHwOaWOJUN34VKyjn8ns0Vh4HVH607FCCRoeUn\n" +
                    "siQLnFeFMO9rgolG1gab0k+3QQKBgQC+KQNXtevvOCMdkmKqtEBxqlU4hjbcwjvt\n" +
                    "JXy5E8yBo3Yu95J8MpPs8xfgDMt1IkDu8Q1afIupLiw40P/LGc5dtIGfgNcGaM5U\n" +
                    "giAGiitNbg1gNjRJdZkkSfQwqphOaDkEKzzgVPFJO0Bck4CVk+CLMFi7UYfZAarv\n" +
                    "iBw3bNtsBQKBgAbHVuDXrai6kYO3GPh9mj8RCSk0VCmY5AxE2/x2tvV32OcdeTX2\n" +
                    "YNm6SXinDQ3jTyHs0CukmwLddVhgVad69xUzyczD5y+q8Vs5ay799FSaiJ/WLjjT\n" +
                    "6XgMsnfeWoHH0nKRV4L+L94a6d0yIbPZUtUifN5+/T3iv2C5wEqJEXdBAoGAJh4w\n" +
                    "hld4ONam61mZn6BMPrynIpJ80tm4i3v1OWBHoUD0Aswrt+unjVrZUVcfPZalL4Bw\n" +
                    "bdADauFPigATFlJQ9KWnSiLYd3dwzK3c35K4xWenFL8o0gTZstWENnEr4KOxsi08\n" +
                    "obdFOqm+6A/FObaPzzbcwofAVHaqHiPJjC6s+UUCgYANOCk9Xee/lBlXpYyerIRu\n" +
                    "POCk9ZDO3wXza5AcSEnv32lyAJDp6SIRlgPdQauP1/odjG4kK0cTV5hElJwo2pdd\n" +
                    "QP8hNDbdJtH9X+m6MeCvZ59XixoO5XZwevuMFV4uk097AvnvkjJ81qZF9C7yHpxE\n" +
                    "KIBleU2U7ISxn4twdx1zMw==" +
                    "-----END PRIVATE KEY-----\n";
            // Using parserBuilder instead of parser and parseClaimsJws for full JWT
            // Generate a SecretKey for HS256 algorithm
            SecretKey key = new SecretKeySpec(privateKeyPEMString.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());

            return Jwts
                    .parserBuilder()
                    .setSigningKey(key) // Use PublicKey to verify RS256 signature
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
