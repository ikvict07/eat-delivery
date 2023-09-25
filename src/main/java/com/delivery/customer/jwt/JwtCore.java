package com.delivery.customer.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtCore {
    @Value("${customer.jwt.duration}")
    private int lifetime;

    @Value("${customer.jwt.secret}")
    private String secret;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String getEmailFromJwt(String jwt) {
        return parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public String getKeyFromJwt(String jwt) {
        return parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

}
