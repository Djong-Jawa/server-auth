package com.server.auth.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyIdGenerator {

    @JsonProperty("id")
    private String id;

    public KeyIdGenerator(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static String generateKeyId(String publicKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(publicKey.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuAw2LmzUFIwVevG+UvNBC3YVDE4nKtAa88KVyEOUsoJMcvXxTegddCevAJTTWln8IIArqGFltj1eLI3t1th9YBkK39Cgosu7GM44UfiFMl4FrtWsG3QIdT/SrEgMVkxb7DhRIKGnwZXcgfALgsO0Hi/TlUREx36E8tWsoxIyFff3IMby7dscHJ4z/51d9TmTdBzIXJLFQ2p7ZV9iOIE5UdrfD4OtyqXiHCqwAHinP+H/HfaiJagWC/h9rAz/6XXh8hTouMiXZzvk2OBK2iJVkcGTioWK0PQGbCZD3q38oPtWMr/nT1V+z0XOHDX2lXZA3sWxL9AR6YYBseQ87XkARQIDAQAB";
        String kid = generateKeyId(publicKey);
        System.out.println("Generated Key ID: " + kid);
    }
}