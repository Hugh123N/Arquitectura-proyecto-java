package com.soft.reclutamiento.infrastructure.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.infrastructure.security.IUserIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Servicio de auditoría para enviar registros al servicio externo
 * Equivalente a AuditoriaService.cs del proyecto .NET
 */
@Service
public class AuditoriaService {

    private final HttpContextReader httpContextReader;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Set<Object> visited = new HashSet<>();

    @Value("${audit.service.url:http://10.147.18.177:9001}")
    private String auditServiceUrl;

    @Value("${audit.service.path:/api/audit/create-list}")
    private String auditServicePath;

    @Value("${audit.service.code:minersoftperu}")
    private String auditServiceCode;

    @Value("${audit.service.token:}")
    private String auditServiceToken;

    @Value("${audit.service.module:Reclutamiento}")
    private String module;

    public AuditoriaService(HttpContextReader httpContextReader) {
        this.httpContextReader = httpContextReader;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Audita una entidad o colección de entidades
     */
    public <T> ResponseDto<Void> auditEntity(Operation operation, String currentUser, T entities) {
        ResponseDto<Void> response = new ResponseDto<>();

        try {
            String url = auditServiceUrl + auditServicePath;

            List<AuditDto> audits = createAuditRecursive(
                    entities,
                    operation,
                    currentUser,
                    httpContextReader.getClientName(),
                    httpContextReader.getClientIp()
            );

            String jsonData = objectMapper.writeValueAsString(audits);

            String jsonResponse = sendJsonPost(url, auditServiceToken, jsonData);

            ResponseDto<?> auditResponse = objectMapper.readValue(jsonResponse, ResponseDto.class);

            if (auditResponse != null) {
                response.attachResults(auditResponse);
            }

        } catch (Exception e) {
            response.addErrorResult("Error al enviar auditoría: " + e.getMessage());
        }

        return response;
    }

    /**
     * Crea recursivamente los registros de auditoría
     */
    private <T> List<AuditDto> createAuditRecursive(
            T entity,
            Operation operation,
            String userName,
            String clientName,
            String clientIP
    ) {
        visited.clear();
        List<AuditDto> audits = new ArrayList<>();
        String entityName = entity.getClass().getSimpleName();

        if (entity instanceof Collection) {
            Collection<?> collection = (Collection<?>) entity;
            int index = 0;
            for (Object item : collection) {
                extractEntityRecursive(
                        item,
                        entityName + "[" + index + "]",
                        operation,
                        userName,
                        clientName,
                        clientIP,
                        audits
                );
                index++;
            }
        } else {
            extractEntityRecursive(entity, entityName, operation, userName, clientName, clientIP, audits);
        }

        return audits;
    }

    /**
     * Extrae recursivamente los datos de una entidad
     */
    private void extractEntityRecursive(
            Object obj,
            String entityName,
            Operation operation,
            String userName,
            String clientName,
            String clientIP,
            List<AuditDto> audits
    ) {
        if (obj == null || visited.contains(obj)) {
            return;
        }

        visited.add(obj);

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            List<AuditDetailDto> details = new ArrayList<>();
            String identifier = "";

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                // Buscar el identificador (campo que empiece con "id")
                if (field.getName().toLowerCase().startsWith("id") && identifier.isEmpty()) {
                    identifier = value != null ? value.toString() : "";
                }

                if (value == null) continue;

                if (isSimple(field.getType())) {
                    details.add(new AuditDetailDto(field.getName(), value.toString()));
                } else if (isCollection(field.getType())) {
                    if (value instanceof Collection) {
                        Collection<?> collection = (Collection<?>) value;
                        int index = 0;
                        for (Object item : collection) {
                            extractEntityRecursive(
                                    item,
                                    entityName + "." + field.getName() + "[" + index + "]",
                                    operation,
                                    userName,
                                    clientName,
                                    clientIP,
                                    audits
                            );
                            index++;
                        }
                    }
                } else if (isClassWithProps(field.getType())) {
                    extractEntityRecursive(
                            value,
                            entityName + "." + field.getName(),
                            operation,
                            userName,
                            clientName,
                            clientIP,
                            audits
                    );
                }
            }

            AuditDto audit = new AuditDto();
            audit.setOperation(operation);
            audit.setEntity(entityName);
            audit.setIdentifier(identifier);
            audit.setUserName(userName);
            audit.setDate(LocalDateTime.now());
            audit.setClientName(clientName);
            audit.setClientIP(clientIP);
            audit.setModule(module);
            audit.setDetails(details);

            audits.add(audit);

        } catch (Exception e) {
            // Log error but continue
        }
    }

    /**
     * Verifica si un tipo es simple (primitivo, String, Date, etc.)
     */
    private boolean isSimple(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || type == String.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Boolean.class
                || type == Byte.class
                || type == Short.class
                || type == Character.class
                || type == java.math.BigDecimal.class
                || type == java.util.Date.class
                || type == java.time.LocalDate.class
                || type == java.time.LocalDateTime.class
                || type == java.time.OffsetDateTime.class
                || type == java.util.UUID.class
                || type == byte[].class;
    }

    /**
     * Verifica si un tipo es una colección
     */
    private boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    /**
     * Verifica si un tipo es una clase con propiedades
     */
    private boolean isClassWithProps(Class<?> type) {
        return !isSimple(type) && !isCollection(type) && !type.isArray();
    }

    /**
     * Envía datos JSON mediante POST
     */
    private String sendJsonPost(String url, String token, String jsonData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (token != null && !token.isEmpty()) {
                headers.setBearerAuth(token);
            }

            HttpEntity<String> request = new HttpEntity<>(jsonData, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar auditoría: " + e.getMessage(), e);
        }
    }
}
