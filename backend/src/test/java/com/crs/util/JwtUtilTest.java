package com.crs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String TEST_SECRET =
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void shouldGenerateAndValidateTenantToken() {
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET, 60, 120);

        String token = jwtUtil.generateToken("admin", 7);

        assertEquals("admin", jwtUtil.extractUsername(token));
        assertEquals(7, jwtUtil.extractTenantId(token));
        assertTrue(jwtUtil.validateToken(token, "admin"));
        assertFalse(jwtUtil.validateToken(token, "other-user"));
    }

    @Test
    void shouldRejectSecretShorterThan256Bits() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("too-short", 60, 120));
    }
}
