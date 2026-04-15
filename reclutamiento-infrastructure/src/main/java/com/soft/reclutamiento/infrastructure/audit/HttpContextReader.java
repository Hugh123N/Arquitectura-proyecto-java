package com.soft.reclutamiento.infrastructure.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servicio para leer información del contexto HTTP
 * Equivalente a HttpContextReader.cs del proyecto .NET
 */
@Component
public class HttpContextReader {

    /**
     * Obtiene el nombre del cliente desde los headers HTTP
     */
    public String getClientName() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String clientName = getHeader(request, "X-Forwarded-Host");
        if (clientName != null && !clientName.isEmpty()) {
            return clientName;
        }

        clientName = getHeader(request, "X-Client-HostName");
        if (clientName != null && !clientName.isEmpty()) {
            return clientName;
        }

        return getClientIp();
    }

    /**
     * Obtiene la IP del cliente desde los headers HTTP
     */
    public String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String clientIp = getHeader(request, "X-Forwarded-For");
        if (clientIp != null && !clientIp.isEmpty()) {
            // X-Forwarded-For puede contener múltiples IPs, tomamos la primera
            return clientIp.split(",")[0].trim();
        }

        clientIp = getHeader(request, "X-Client-IP");
        if (clientIp != null && !clientIp.isEmpty()) {
            return clientIp;
        }

        return request.getRemoteAddr();
    }

    private String getHeader(HttpServletRequest request, String headerName) {
        return request.getHeader(headerName);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
