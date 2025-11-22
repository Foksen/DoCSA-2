package ru.mirea.docsa2.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthenticationUtil {

    public static String extractUsername(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return "anonymous";
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }
        return authentication.getName();
    }

    public static Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object userId = jwt.getClaims().get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }
}

