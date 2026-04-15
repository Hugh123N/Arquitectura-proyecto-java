package com.soft.reclutamiento.infrastructure.security;

import java.util.Map;
import java.util.UUID;

/**
 * Interface para identidad de usuario
 * Equivalente a IUserIdentity.cs del proyecto .NET
 * Obtiene información del usuario actual desde el token JWT
 */
public interface IUserIdentity {

    Map<String, Object> getClaims();

    String getCurrentToken();

    <T> T getClaim(String type, Class<T> clazz);

    String getApplicationCode();

    String getUserName();

    String getCurrentUser();

    UUID getCurrentUserId();
}
