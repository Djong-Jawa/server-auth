package com.server.auth.config;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureConvertor {

    // Example method to convert Base64 encoded string to PublicKey
    public PublicKey getPublicKeyFromString(String publicKeyStr) throws Exception {
        // Remove any PEM header/footer if present (e.g., "-----BEGIN PUBLIC KEY-----")
        publicKeyStr = publicKeyStr.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove any whitespace

        // Decode Base64-encoded string
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);

        // Create a KeyFactory and use it to convert the key bytes into a PublicKey object
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  // Specify the algorithm (RSA in this case)
        return keyFactory.generatePublic(spec);
    }

    public PrivateKey getPrivateKeyFromString(String privateKeyStr) throws Exception {
        // Remove the PEM headers if present (e.g., "-----BEGIN PRIVATE KEY-----")
        privateKeyStr = privateKeyStr.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Remove any whitespace

        // Decode the Base64 string to get the binary data
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);

        // Create a PKCS8EncodedKeySpec with the decoded key bytes
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        // Create a KeyFactory for the RSA algorithm and generate a PrivateKey object
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  // Specify the key algorithm (RSA in this case)
        return keyFactory.generatePrivate(spec);
    }
}
