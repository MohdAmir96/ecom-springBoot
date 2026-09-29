package com.amir.ecommerce.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTests {
    private static final String SECRET = Base64.getEncoder()
            .encodeToString("a-long-test-secret-with-at-least-32-bytes".getBytes());

    @Test
    void generatedTokenContainsUsername() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String token = jwtService.generateToken("amir");

        assertEquals("amir", jwtService.extractUsername(token));
        org.junit.jupiter.api.Assertions.assertTrue(jwtService.isTokenValid(token, "amir"));
    }

    @Test
    void rejectsTokenSignedByDifferentKey() {
        JwtService issuer = new JwtService(SECRET, 60_000);
        JwtService verifier = new JwtService(
                Base64.getEncoder().encodeToString("a-different-secret-that-is-also-over-32-bytes".getBytes()),
                60_000);

        String token = issuer.generateToken("amir");

        assertThrows(JwtException.class, () -> verifier.extractUsername(token));
    }
}
