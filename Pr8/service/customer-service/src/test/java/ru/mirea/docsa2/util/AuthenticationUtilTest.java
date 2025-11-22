package ru.mirea.docsa2.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationUtilTest {

    @Test
    void extractUsername_WithJwtPrincipal_ShouldReturnSubClaim() {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("sub")).thenReturn("testuser");

        String result = AuthenticationUtil.extractUsername(auth);

        assertEquals("testuser", result);
    }

    @Test
    void extractUsername_WithNullAuthentication_ShouldReturnAnonymous() {
        String result = AuthenticationUtil.extractUsername(null);

        assertEquals("anonymous", result);
    }

    @Test
    void extractUserId_WithLongUserId_ShouldReturnLong() {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(Map.of("userId", 100L));

        Long result = AuthenticationUtil.extractUserId(auth);

        assertEquals(100L, result);
    }

    @Test
    void extractUserId_WithIntegerUserId_ShouldConvertToLong() {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(Map.of("userId", 100));

        Long result = AuthenticationUtil.extractUserId(auth);

        assertEquals(100L, result);
    }

    @Test
    void extractUserId_WithNullAuthentication_ShouldReturnNull() {
        Long result = AuthenticationUtil.extractUserId(null);

        assertNull(result);
    }
}

