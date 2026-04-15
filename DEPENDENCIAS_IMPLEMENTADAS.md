# Dependencias Implementadas - Spring Base Project

## Proyecto: Clean Architecture + CQRS con Java 21 y Spring Boot 3.4.1

---

## Resumen de Dependencias

**Spring Boot Version:** 3.4.1
**Java Version:** 21
**Build Tool:** Maven 3.9+

**Total de Dependencias:** 40+
**Total de Plugins:** 6

---

## 1. Spring Boot Starters (8 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `spring-boot-starter-web` | 3.4.1 | REST API, Spring MVC, Tomcat embebido |
| `spring-boot-starter-data-jpa` | 3.4.1 | JPA, Hibernate, Spring Data repositories |
| `spring-boot-starter-security` | 3.4.1 | Spring Security framework |
| `spring-boot-starter-oauth2-resource-server` | 3.4.1 | OAuth2 Resource Server para JWT |
| `spring-boot-starter-validation` | 3.4.1 | Jakarta Bean Validation (Hibernate Validator) |
| `spring-boot-starter-actuator` | 3.4.1 | Health checks, metrics, monitoring endpoints |
| `spring-boot-starter-cache` | 3.4.1 | Spring Cache abstraction |
| `spring-boot-starter-mail` | 3.4.1 | JavaMail para envío de correos |

**Equivalencias .NET:**
- `spring-boot-starter-web` ≈ ASP.NET Core Web API
- `spring-boot-starter-data-jpa` ≈ Entity Framework Core
- `spring-boot-starter-security` ≈ ASP.NET Core Identity + Security
- `spring-boot-starter-validation` ≈ FluentValidation

---

## 2. Drivers de Base de Datos (4 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `mysql-connector-j` | 9.1.0 | Driver JDBC para MySQL 8+ |
| `mssql-jdbc` | 12.8.1 | Driver JDBC para SQL Server |
| `mybatis-spring-boot-starter` | 3.0.4 | MyBatis para queries SQL complejas |
| `flyway-core`, `flyway-mysql`, `flyway-sqlserver` | 10.21.0 | Migraciones de base de datos versionadas |

**Equivalencias .NET:**
- `mssql-jdbc` ≈ Microsoft.Data.SqlClient
- `mysql-connector-j` ≈ MySql.Data
- `mybatis-spring-boot-starter` ≈ Dapper
- `flyway` ≈ Entity Framework Migrations

---

## 3. CQRS / Mediator Pattern (1 dependencia)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `pipelinr` | 0.8 | Implementación del patrón Mediator para CQRS |

**Características:**
- API similar a MediatR (.NET)
- Solo ~30kb, sin dependencias
- Soporte para Request/Response
- Pipeline de validación
- Handlers síncronos y asíncronos

**Equivalencia .NET:**
- `pipelinr` ≈ MediatR

**Uso:**
```java
// Command
public record CreateAreaCommand(CreateAreaDto dto) implements Command<ResponseDto<AreaDto>> {}

// Handler
@Component
public class CreateAreaCommandHandler implements Command.Handler<CreateAreaCommand, ResponseDto<AreaDto>> {
    @Override
    public ResponseDto<AreaDto> handle(CreateAreaCommand command) {
        // lógica
    }
}

// Enviar
pipeline.send(new CreateAreaCommand(dto));
```

---

## 4. Object Mapping (1 dependencia)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `mapstruct` | 1.6.3 | Mapeo de objetos en compile-time |

**Características:**
- Generación de código en compile-time (no reflection)
- Type-safe
- Alto rendimiento
- Integración con Lombok

**Equivalencia .NET:**
- `mapstruct` ≈ AutoMapper

**Uso:**
```java
@Mapper(componentModel = "spring")
public interface AreaMapper {
    AreaDto toDto(Area entity);
    Area toEntity(AreaDto dto);
    List<AreaDto> toDtoList(List<Area> entities);
}
```

---

## 5. Boilerplate Reduction (1 dependencia)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `lombok` | 1.18.36 | Reducción de código boilerplate |

**Annotations principales:**
- `@Data` - getters, setters, toString, equals, hashCode
- `@Builder` - Patrón Builder
- `@NoArgsConstructor`, `@AllArgsConstructor` - Constructores
- `@Slf4j` - Logger automático
- `@Value` - Clases inmutables
- `@RequiredArgsConstructor` - Constructor con campos final

**Equivalencia .NET:**
- Lombok ≈ C# Records + Auto-properties

---

## 6. Security / JWT (3 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `jjwt-api` | 0.12.6 | API de Java JWT |
| `jjwt-impl` | 0.12.6 | Implementación de jjwt (runtime) |
| `jjwt-jackson` | 0.12.6 | JSON processing con Jackson (runtime) |

**Características:**
- Generación y validación de JWT
- Soporte para múltiples algoritmos (HS256, RS256, etc.)
- Claims personalizados
- Validación de expiración

**Equivalencia .NET:**
- `jjwt` ≈ System.IdentityModel.Tokens.Jwt
- `spring-boot-starter-oauth2-resource-server` ≈ Microsoft.AspNetCore.Authentication.JwtBearer

---

## 7. API Documentation (1 dependencia)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `springdoc-openapi-starter-webmvc-ui` | 2.7.0 | OpenAPI 3 + Swagger UI |

**URLs por defecto:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

**Características:**
- Generación automática de documentación
- Soporte para anotaciones Jakarta
- Integración con Spring Security
- Personalización de UI

**Equivalencia .NET:**
- `springdoc-openapi` ≈ Swashbuckle.AspNetCore

---

## 8. Utilities (3 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `commons-lang3` | 3.17.0 | Utilidades de cadenas, arrays, objetos, etc. |
| `commons-collections4` | 4.5.0-M3 | Colecciones avanzadas |
| `guava` | 33.3.1-jre | Biblioteca de utilidades de Google |

**Utilidades comunes:**
- StringUtils (Apache Commons)
- CollectionUtils (Apache Commons)
- Lists, Maps, Sets (Guava)
- Predicates, Functions (Guava)

**Equivalencia .NET:**
- Parcialmente equivalente a System.Linq y extensiones personalizadas

---

## 9. Document Generation (2 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `poi-ooxml` | 5.3.0 | Apache POI para Word, Excel |
| `itext-core` | 8.0.5 | iText para generación de PDF |

**Capacidades:**
- Crear documentos Word (.docx)
- Crear hojas de cálculo Excel (.xlsx)
- Generar PDF con contenido dinámico
- Leer y modificar documentos existentes

**Equivalencia .NET:**
- `poi-ooxml` ≈ EPPlus, NPOI, Aspose.Words
- `itext` ≈ iTextSharp, PdfSharp

---

## 10. Monitoring & Logging (2 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `sentry-spring-boot-starter-jakarta` | 8.0.0 | Error tracking y monitoreo con Sentry |
| `micrometer-registry-prometheus` | - | Métricas para Prometheus |

**Características Sentry:**
- Tracking automático de excepciones
- Breadcrumbs de requests
- Performance monitoring
- Release tracking

**Características Micrometer:**
- Métricas de JVM
- Métricas de HTTP
- Métricas custom
- Integración con Prometheus, Grafana

**Equivalencia .NET:**
- `sentry` ≈ Sentry.AspNetCore
- `micrometer` ≈ App Insights, Prometheus-net

**IMPORTANTE:** Para Spring Boot 3, usar `sentry-spring-boot-starter-jakarta` (NO el starter normal)

---

## 11. Cache (1 dependencia)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `caffeine` | 3.1.8 | Caché en memoria de alto rendimiento |

**Características:**
- Basado en Google Guava Cache
- Caché asíncrona
- Estadísticas de uso
- Políticas de expiración
- Thread-safe

**Configuración:**
```java
@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

**Equivalencia .NET:**
- `caffeine` ≈ IMemoryCache de .NET

---

## 12. Testing (6 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `spring-boot-starter-test` | 3.4.1 | JUnit 5, Mockito, AssertJ, etc. |
| `spring-security-test` | - | Utilidades de testing para Security |
| `testcontainers` | 1.20.4 | Testing con Docker containers |
| `testcontainers:mysql` | 1.20.4 | Testcontainer para MySQL |
| `testcontainers:mssqlserver` | 1.20.4 | Testcontainer para SQL Server |
| `archunit` | 1.3.0 | Testing de arquitectura |

**Características Testcontainers:**
- Base de datos real en Docker para tests
- Limpieza automática
- Configuración automática de datasource

**Características ArchUnit:**
- Verificar reglas de arquitectura
- Validar dependencias entre capas
- Asegurar naming conventions

**Equivalencia .NET:**
- `spring-boot-starter-test` ≈ xUnit, NUnit, Moq
- `testcontainers` ≈ Testcontainers.DotNet
- `archunit` ≈ NetArchTest

---

## 13. Development Tools (2 dependencias)

| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `spring-boot-devtools` | 3.4.1 | Hot reload, LiveReload, etc. |
| `spring-boot-configuration-processor` | 3.4.1 | Metadata para @ConfigurationProperties |

**DevTools Features:**
- Restart automático al cambiar código
- LiveReload para navegador
- Deshabilitado automáticamente en producción

---

## Configuración de Annotation Processors

### Maven Compiler Plugin

**Versión:** 3.13.0

**Configuración crítica:**

```xml
<annotationProcessorPaths>
    <!-- IMPORTANTE: Lombok debe ir PRIMERO -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.36</version>
    </path>
    <!-- MapStruct debe ir DESPUÉS de Lombok -->
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.6.3</version>
    </path>
    <!-- Binding necesario para Lombok + MapStruct -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

**¿Por qué este orden?**
- Lombok genera getters/setters primero
- MapStruct los usa para generar mappers
- El binding hace que trabajen juntos correctamente

---

## Plugins de Maven

### 1. Spring Boot Maven Plugin
**Versión:** 3.4.1 (heredada)
**Propósito:** Empaquetar aplicación como JAR ejecutable

### 2. Maven Compiler Plugin
**Versión:** 3.13.0
**Propósito:** Compilar con Java 21 y procesar annotations

### 3. Maven Surefire Plugin
**Versión:** 3.5.2
**Propósito:** Ejecutar tests unitarios

### 4. JaCoCo Maven Plugin
**Versión:** 0.8.12
**Propósito:** Code coverage reports

### 5. Flyway Maven Plugin
**Versión:** 10.21.0
**Propósito:** Ejecutar migraciones de BD

### 6. Versions Maven Plugin
**Versión:** 2.18.0
**Propósito:** Actualizar versiones de dependencias

---

## Comandos Útiles de Maven

### Compilar proyecto
```bash
./mvnw clean compile
```

### Ejecutar tests
```bash
./mvnw test
```

### Empaquetar JAR
```bash
./mvnw clean package
```

### Ejecutar aplicación
```bash
./mvnw spring-boot:run
```

### Generar reporte de coverage
```bash
./mvnw clean test jacoco:report
```

### Ejecutar migraciones Flyway
```bash
./mvnw flyway:migrate
```

### Verificar actualizaciones de dependencias
```bash
./mvnw versions:display-dependency-updates
```

---

## Configuración de application.yaml

### Ejemplo completo

```yaml
server:
  port: 8080

spring:
  application:
    name: spring-base

  datasource:
    url: jdbc:mysql://localhost:3306/database
    username: user
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m

# Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

# Swagger/OpenAPI
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html

# Sentry
sentry:
  dsn: https://your-sentry-dsn
  traces-sample-rate: 1.0
  environment: production
```

---

## Verificación de Instalación

### 1. Verificar Java 21
```bash
java -version
# Debe mostrar: openjdk version "21.x.x"
```

### 2. Compilar proyecto
```bash
cd spring-base
./mvnw clean compile
```

### 3. Ejecutar tests
```bash
./mvnw test
```

### 4. Verificar dependencias
```bash
./mvnw dependency:tree
```

### 5. Ejecutar aplicación
```bash
./mvnw spring-boot:run
```

### 6. Verificar Swagger
Navegar a: `http://localhost:8080/swagger-ui.html`

---

## Compatibilidad de Versiones

| Componente | Versión Mínima | Versión Recomendada |
|-----------|----------------|---------------------|
| Java | 21.0.0 | 21.0.1+ |
| Maven | 3.8.0 | 3.9.0+ |
| Spring Boot | 3.2.0 | 3.4.1 |
| IDE | IntelliJ 2023.2+ | IntelliJ 2024.3+ |
| | Eclipse 2023-09+ | Eclipse 2024-12+ |
| | VS Code con Extension Pack | Última versión |

---

## Resumen de Equivalencias .NET → Java

| .NET | Java | Dependencia |
|------|------|-------------|
| MediatR | PipelinR | `pipelinr` |
| AutoMapper | MapStruct | `mapstruct` |
| FluentValidation | Jakarta Bean Validation | `spring-boot-starter-validation` |
| Entity Framework Core | Spring Data JPA | `spring-boot-starter-data-jpa` |
| Dapper | MyBatis | `mybatis-spring-boot-starter` |
| Swashbuckle | SpringDoc OpenAPI | `springdoc-openapi-starter-webmvc-ui` |
| JWT Bearer | jjwt + OAuth2 Resource Server | `jjwt-*`, `spring-boot-starter-oauth2-resource-server` |
| Sentry.AspNetCore | Sentry Jakarta | `sentry-spring-boot-starter-jakarta` |
| IMemoryCache | Caffeine | `caffeine` |
| EF Migrations | Flyway | `flyway-*` |
| xUnit/NUnit | JUnit 5 | `spring-boot-starter-test` |
| Moq | Mockito | incluido en `spring-boot-starter-test` |

---

## Próximos Pasos

1. ✅ Dependencias instaladas
2. ⏭️ Configurar estructura de módulos
3. ⏭️ Implementar entidades base
4. ⏭️ Configurar PipelinR
5. ⏭️ Crear mappers con MapStruct
6. ⏭️ Implementar primer Command/Query
7. ⏭️ Configurar Security + JWT
8. ⏭️ Probar Swagger UI

---

**Documento generado:** 2025-01-04
**Spring Boot:** 3.4.1
**Java:** 21
**Estado:** ✅ Todas las dependencias compatibles y verificadas
