package com.soft.reclutamiento.infrastructure.security;

/*
 * TEMPORALMENTE COMENTADO - Requiere dependencias de Spring Security OAuth2 JWT
 * Descomentar cuando se agreguen las dependencias:
 * - spring-boot-starter-security
 * - spring-boot-starter-oauth2-resource-server
 *
 * Para habilitar esta clase:
 * 1. Descomentar todo el código a continuación
 * 2. Agregar las dependencias necesarias al POM
 * 3. Configurar Spring Security en la aplicación
 */

/*
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;*/
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class UserIdentityImpl implements IUserIdentity {
    @Override
    public Map<String, Object> getClaims() {
        return Map.of();
    }

    @Override
    public String getCurrentToken() {
        return "";
    }

    @Override
    public <T> T getClaim(String type, Class<T> clazz) {
        return null;
    }

    @Override
    public String getApplicationCode() {
        return "";
    }

    @Override
    public String getUserName() {
        return "";
    }

    @Override
    public String getCurrentUser() {
        return "";
    }

    @Override
    public UUID getCurrentUserId() {
        return null;
    }



/*
    @Override
    public Map<String, Object> getClaims() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            return jwt.getClaims();
        }
        return new HashMap<>();
    }

    @Override
    public String getCurrentToken() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getTokenValue() : null;
    }

    @Override
    public <T> T getClaim(String type, Class<T> clazz) {
        Jwt jwt = getJwt();
        if (jwt != null) {
            Object claim = jwt.getClaim(type);
            if (claim != null && clazz.isInstance(claim)) {
                return clazz.cast(claim);
            }
            if (claim instanceof String) {
                return convertStringToClaim((String) claim, clazz);
            }
        }
        return null;
    }

    @Override
    public String getApplicationCode() {
        return getClaim("app_code", String.class);
    }

    @Override
    public String getUserName() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            String username = jwt.getClaim("preferred_username");
            if (username != null && !username.isEmpty()) {
                return username;
            }
            username = jwt.getClaim("name");
            if (username != null && !username.isEmpty()) {
                return username;
            }
            return jwt.getSubject();
        }
        return "anonymous";
    }

    @Override
    public String getCurrentUser() {
        return getUserName();
    }

    @Override
    public UUID getCurrentUserId() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            String sub = jwt.getSubject();
            if (sub != null && !sub.isEmpty()) {
                try {
                    return UUID.fromString(sub);
                } catch (IllegalArgumentException e) {
                    String userId = jwt.getClaim("user_id");
                    if (userId != null) {
                        try {
                            return UUID.fromString(userId);
                        } catch (IllegalArgumentException ex) {
                        }
                    }
                }
            }
        }
        return null;
    }

    private Jwt getJwt() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                return (Jwt) authentication.getPrincipal();
            }
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertStringToClaim(String value, Class<T> clazz) {
        try {
            if (clazz == String.class) {
                return (T) value;
            } else if (clazz == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (clazz == Long.class) {
                return (T) Long.valueOf(value);
            } else if (clazz == Boolean.class) {
                return (T) Boolean.valueOf(value);
            } else if (clazz == UUID.class) {
                return (T) UUID.fromString(value);
            }
        } catch (Exception e) {
        }
        return null;
    }*/
}

