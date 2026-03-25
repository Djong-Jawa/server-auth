package com.server.auth.controller;

import com.server.auth.config.KeyIdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(".well-known")
public class JwksController {

    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() throws NoSuchAlgorithmException {
        // Example public key in Base64 format (replace with your actual public key)
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuAw2LmzUFIwVevG+UvNBC3YVDE4nKtAa88KVyEOUsoJMcvXxTegddCevAJTTWln8IIArqGFltj1eLI3t1th9YBkK39Cgosu7GM44UfiFMl4FrtWsG3QIdT/SrEgMVkxb7DhRIKGnwZXcgfALgsO0Hi/TlUREx36E8tWsoxIyFff3IMby7dscHJ4z/51d9TmTdBzIXJLFQ2p7ZV9iOIE5UdrfD4OtyqXiHCqwAHinP+H/HfaiJagWC/h9rAz/6XXh8hTouMiXZzvk2OBK2iJVkcGTioWK0PQGbCZD3q38oPtWMr/nT1V+z0XOHDX2lXZA3sWxL9AR6YYBseQ87XkARQIDAQAB";

        // Create a JWKS response
        Map<String, Object> key = new HashMap<>();
        key.put("kty", "RSA");
        key.put("alg", "RS256");
        key.put("use", "sig");

        key.put("kid", KeyIdGenerator.generateKeyId(publicKey)); // Key ID (replace with your actual key ID)
        key.put("n", Base64.getUrlEncoder().encodeToString(Base64.getDecoder().decode(publicKey)));
        key.put("e", "AQAB"); // Public exponent

        return Collections.singletonMap("keys", Collections.singletonList(key));
    }
}
