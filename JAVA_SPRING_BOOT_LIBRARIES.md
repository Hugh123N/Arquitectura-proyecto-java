# Librerías Java/Spring Boot para Arquitectura Clean + CQRS

## Comparación .NET vs Java/Spring Boot

Documento completo de mapeo de tecnologías para implementar la misma arquitectura del proyecto Reclutamiento (.NET) en Java 21 con Spring Boot 3.

---

## Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Comparación Tecnológica](#comparación-tecnológica)
3. [Dependencias Maven Completas](#dependencias-maven-completas)
4. [Dependencias Gradle Completas](#dependencias-gradle-completas)
5. [Configuración de Annotation Processors](#configuración-de-annotation-processors)
6. [Estructura de Proyecto Multi-módulo](#estructura-de-proyecto-multi-módulo)
7. [Configuración de Plugins](#configuración-de-plugins)
8. [Consideraciones de Arquitectura](#consideraciones-de-arquitectura)

---

## Resumen Ejecutivo

### Stack Tecnológico Java 21 + Spring Boot 3.3+

**Framework Base:**
- Java 21 LTS
- Spring Boot 3.3.x (última versión estable)
- Spring Framework 6.1.x
- Jakarta EE 10

**Build Tool:** Maven 3.9+ o Gradle 8.5+

---

## Comparación Tecnológica

### 1. Framework Principal

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| ASP.NET Core 8.0 | Spring Boot | 3.3.6 | Framework web principal |
| .NET 8.0 | Java | 21 LTS | Runtime y lenguaje |
| C# 12 | Java | 21 | Lenguaje de programación |

---

### 2. ORM y Acceso a Datos

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Entity Framework Core | Spring Data JPA | 3.3.x | ORM principal con auto-configuración |
| EF Core 8.0.6 | Hibernate | 6.4.x | Implementación JPA (viene con Spring Data JPA) |
| Dapper | JdbcTemplate / MyBatis | - | Micro-ORM para queries complejas |
| - | QueryDSL | 5.1.0 | Type-safe queries (opcional) |

**Dependencias:**
```xml
<!-- Spring Data JPA (incluye Hibernate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Driver SQL Server -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- MyBatis (opcional para queries complejas tipo Dapper) -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

---

### 3. CQRS y Mediator Pattern

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| MediatR | PipelinR | 0.8 | Recomendado: lightweight, sin dependencias |
| - | java-mediator | 1.0+ | Alternativa: inspirado directamente en MediatR |
| - | Axon Framework | 4.10.x | Para Event Sourcing completo (más pesado) |

**Dependencia Recomendada - PipelinR:**
```xml
<dependency>
    <groupId>net.sizovs</groupId>
    <artifactId>pipelinr</artifactId>
    <version>0.8</version>
</dependency>
```

**Alternativa - java-mediator:**
```xml
<dependency>
    <groupId>com.github.mehdihadeli</groupId>
    <artifactId>java-mediator-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

**PipelinR es la mejor opción porque:**
- Solo ~30kb
- Sin dependencias externas
- API similar a MediatR
- Soporte para pipelines de validación
- Compatible con Spring Boot 3

---

### 4. Mapeo de Objetos (AutoMapper)

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| AutoMapper | MapStruct | 1.6.3 | Generación de código en compile-time |
| - | ModelMapper | 3.2.1 | Alternativa: mapeo en runtime |

**Dependencia Recomendada - MapStruct:**
```xml
<properties>
    <mapstruct.version>1.6.3</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
</dependencies>
```

**MapStruct es mejor que ModelMapper porque:**
- Generación de código en compile-time (más rápido)
- Type-safe
- Mejor rendimiento (sin reflection)
- Detección de errores en compilación
- Integración perfecta con Lombok

---

### 5. Validación (FluentValidation)

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| FluentValidation | Jakarta Bean Validation (JSR-380) | 3.0.2 | API estándar de validación |
| - | Hibernate Validator | 8.0.1 | Implementación de referencia |
| - | Spring Validation | - | Integración con Spring |

**Dependencias (incluidas en spring-boot-starter-validation):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Nota:** Jakarta Bean Validation usa anotaciones (@NotNull, @Size, @Email, etc.) en lugar de la sintaxis fluent de .NET. Para una API más fluent, puedes considerar:

```xml
<!-- Bean Validation Helper (opcional) -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-validation</artifactId>
    <version>13.5</version>
</dependency>
```

---

### 6. Seguridad y Autenticación

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| IdentityServer4 | Spring Security OAuth2 Resource Server | - | Validación de JWT |
| JWT Bearer Authentication | Spring Security JWT | - | Autenticación con tokens |
| ASP.NET Core Identity | Spring Security + JPA | - | Gestión de usuarios |
| - | jjwt (Java JWT) | 0.12.6 | Generación y validación de JWT |

**Dependencias:**
```xml
<!-- Spring Security con JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- Librería JWT (jjwt) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

---

### 7. Documentación de API (Swagger/OpenAPI)

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Swashbuckle.AspNetCore | springdoc-openapi | 2.7.0 | OpenAPI 3 + Swagger UI |
| - | SpringFox | ❌ Deprecated | NO usar, no soporta Spring Boot 3 |

**Dependencia:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

**URLs por defecto:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

### 8. Serialización JSON

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Newtonsoft.Json / System.Text.Json | Jackson | 2.18.x | Viene con Spring Boot |
| - | Gson | 2.11.0 | Alternativa de Google (opcional) |

**Dependencia (ya incluida en spring-boot-starter-web):**
```xml
<!-- Jackson viene automáticamente con Spring Boot -->
<!-- Solo si necesitas funciones adicionales: -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
</dependency>
```

---

### 9. Auditoría Automática

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Custom AuditingEntityListener | Spring Data JPA Auditing | - | @CreatedBy, @LastModifiedBy, @CreatedDate, @LastModifiedDate |
| - | Hibernate Envers | 6.4.x | Auditoría completa con versionado (opcional) |

**Configuración (ya incluido en Spring Data JPA):**
```java
@EnableJpaAuditing // En tu clase de configuración
```

**Annotations en entidades:**
```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Auditable {
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}
```

---

### 10. Monitoreo y Logging

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Sentry.AspNetCore | sentry-spring-boot-starter-jakarta | 8.0.0+ | Para Spring Boot 3 usar variant Jakarta |
| Elastic APM | elastic-apm-agent | 1.52.x | Java Agent para APM |
| Serilog / NLog | Logback / SLF4J | - | Logging (viene con Spring Boot) |
| - | Micrometer | - | Métricas (incluido en Spring Boot Actuator) |

**Dependencias:**
```xml
<!-- Sentry para Spring Boot 3 -->
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
    <version>8.0.0</version>
</dependency>

<!-- Spring Boot Actuator (métricas, health checks) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Registry para Prometheus (opcional) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Elastic APM (Java Agent - no es dependencia Maven):**
```bash
java -javaagent:/path/to/elastic-apm-agent-1.52.0.jar \
     -Delastic.apm.service_name=reclutamiento-api \
     -Delastic.apm.server_urls=http://localhost:8200 \
     -jar your-application.jar
```

---

### 11. Utilidades

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Custom Utilities | Apache Commons Lang | 3.17.0 | Utilidades generales |
| - | Apache Commons Collections | 4.5.0-M3 | Colecciones avanzadas |
| - | Guava | 33.3.1-jre | Utilidades de Google |
| Aspose.Words | Apache POI | 5.3.0 | Manipulación de Word/Excel |
| - | iText | 8.0.5 | Generación de PDFs |

**Dependencias:**
```xml
<!-- Utilidades comunes -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.17.0</version>
</dependency>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.5.0-M3</version>
</dependency>

<!-- Guava (opcional) -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>33.3.1-jre</version>
</dependency>

<!-- Apache POI para Word/Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>

<!-- iText para PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>8.0.5</version>
    <type>pom</type>
</dependency>
```

---

### 12. Boilerplate Reduction (Lombok)

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| - | Lombok | 1.18.36 | Reducción de código boilerplate |

**Dependencia:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
    <scope>provided</scope>
</dependency>
```

**Annotations comunes:**
- `@Data` - Genera getters, setters, toString, equals, hashCode
- `@Builder` - Patrón Builder
- `@NoArgsConstructor`, `@AllArgsConstructor` - Constructores
- `@Slf4j` - Logger automático
- `@Value` - Clases inmutables

---

### 13. Testing

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| xUnit / NUnit | JUnit | 5.11.x | Framework de testing (incluido en Spring Boot) |
| Moq | Mockito | 5.15.x | Mocking (incluido en Spring Boot) |
| - | AssertJ | 3.27.x | Assertions fluent |
| - | Testcontainers | 1.20.x | Testing con containers Docker |

**Dependencias:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers (para integración con SQL Server real) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mssqlserver</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

---

### 14. Cache

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| IMemoryCache | Spring Cache Abstraction | - | Caché en memoria |
| - | Caffeine | 3.1.8 | Implementación de caché de alto rendimiento |
| - | Redis | - | Caché distribuido (opcional) |

**Dependencias:**
```xml
<!-- Spring Cache + Caffeine -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>

<!-- Redis (opcional para caché distribuido) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

---

### 15. Email Client

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Custom SMTP Client | Spring Boot Mail | - | Cliente SMTP integrado |
| - | Jakarta Mail | 2.0.1 | API de email |

**Dependencia:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

### 16. Dependency Injection

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| Microsoft.Extensions.DependencyInjection | Spring IoC Container | - | DI nativo de Spring |
| Scrutor (assembly scanning) | Spring Component Scanning | - | @ComponentScan automático |

**No requiere dependencias adicionales** - es parte del core de Spring Boot.

---

### 17. Configuration Management

| .NET | Java/Spring Boot | Versión | Notas |
|------|------------------|---------|-------|
| IConfiguration / appsettings.json | @ConfigurationProperties | - | Binding de configuración |
| - | Spring Cloud Config | 4.1.x | Config Server (opcional) |

**Dependencias (opcional para config avanzado):**
```xml
<!-- Spring Cloud Config Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- Para YAML avanzado -->
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.3</version>
</dependency>
```

---

## Dependencias Maven Completas

### pom.xml Parent (Root del multi-módulo)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.empresa</groupId>
    <artifactId>reclutamiento</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>Sistema de Reclutamiento</name>
    <description>Sistema de gestión de reclutamiento - Clean Architecture + CQRS</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.6</version>
        <relativePath/>
    </parent>

    <modules>
        <module>reclutamiento-domain</module>
        <module>reclutamiento-application</module>
        <module>reclutamiento-infrastructure</module>
        <module>reclutamiento-api</module>
        <module>reclutamiento-common</module>
    </modules>

    <properties>
        <!-- Java Version -->
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Spring Cloud -->
        <spring-cloud.version>2023.0.3</spring-cloud.version>

        <!-- CQRS / Mediator -->
        <pipelinr.version>0.8</pipelinr.version>

        <!-- Mapping -->
        <mapstruct.version>1.6.3</mapstruct.version>
        <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>

        <!-- Lombok -->
        <lombok.version>1.18.36</lombok.version>

        <!-- JWT -->
        <jjwt.version>0.12.6</jjwt.version>

        <!-- OpenAPI/Swagger -->
        <springdoc.version>2.7.0</springdoc.version>

        <!-- Utilities -->
        <commons-lang3.version>3.17.0</commons-lang3.version>
        <commons-collections4.version>4.5.0-M3</commons-collections4.version>
        <guava.version>33.3.1-jre</guava.version>

        <!-- Documents -->
        <poi.version>5.3.0</poi.version>
        <itext.version>8.0.5</itext.version>

        <!-- Monitoring -->
        <sentry.version>8.0.0</sentry.version>

        <!-- Cache -->
        <caffeine.version>3.1.8</caffeine.version>

        <!-- Testing -->
        <testcontainers.version>1.20.4</testcontainers.version>
        <archunit.version>1.3.0</archunit.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud BOM -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Internal Modules -->
            <dependency>
                <groupId>com.empresa</groupId>
                <artifactId>reclutamiento-domain</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.empresa</groupId>
                <artifactId>reclutamiento-application</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.empresa</groupId>
                <artifactId>reclutamiento-infrastructure</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.empresa</groupId>
                <artifactId>reclutamiento-common</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- PipelinR (MediatR equivalent) -->
            <dependency>
                <groupId>net.sizovs</groupId>
                <artifactId>pipelinr</artifactId>
                <version>${pipelinr.version}</version>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- JWT -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>

            <!-- OpenAPI/Swagger -->
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>${springdoc.version}</version>
            </dependency>

            <!-- Utilities -->
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>${commons-lang3.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-collections4</artifactId>
                <version>${commons-collections4.version}</version>
            </dependency>
            <dependency>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
                <version>${guava.version}</version>
            </dependency>

            <!-- Documents -->
            <dependency>
                <groupId>org.apache.poi</groupId>
                <artifactId>poi-ooxml</artifactId>
                <version>${poi.version}</version>
            </dependency>
            <dependency>
                <groupId>com.itextpdf</groupId>
                <artifactId>itext7-core</artifactId>
                <version>${itext.version}</version>
                <type>pom</type>
            </dependency>

            <!-- Sentry -->
            <dependency>
                <groupId>io.sentry</groupId>
                <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
                <version>${sentry.version}</version>
            </dependency>

            <!-- Caffeine Cache -->
            <dependency>
                <groupId>com.github.ben-manes.caffeine</groupId>
                <artifactId>caffeine</artifactId>
                <version>${caffeine.version}</version>
            </dependency>

            <!-- Testcontainers -->
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- ArchUnit (Architecture testing) -->
            <dependency>
                <groupId>com.tngtech.archunit</groupId>
                <artifactId>archunit</artifactId>
                <version>${archunit.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>

                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.13.0</version>
                    <configuration>
                        <source>21</source>
                        <target>21</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok-mapstruct-binding</artifactId>
                                <version>${lombok-mapstruct-binding.version}</version>
                            </path>
                        </annotationProcessorPaths>
                        <compilerArgs>
                            <arg>-parameters</arg>
                        </compilerArgs>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

---

### pom.xml Módulo API (reclutamiento-api)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.empresa</groupId>
        <artifactId>reclutamiento</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>reclutamiento-api</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Internal Modules -->
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-application</artifactId>
        </dependency>
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-infrastructure</artifactId>
        </dependency>
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-common</artifactId>
        </dependency>

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- OpenAPI/Swagger -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
        </dependency>

        <!-- Sentry -->
        <dependency>
            <groupId>io.sentry</groupId>
            <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
        </dependency>

        <!-- Caffeine Cache -->
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>

        <!-- Micrometer Prometheus -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### pom.xml Módulo Application

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.empresa</groupId>
        <artifactId>reclutamiento</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>reclutamiento-application</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Internal Module -->
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-domain</artifactId>
        </dependency>

        <!-- PipelinR (MediatR) -->
        <dependency>
            <groupId>net.sizovs</groupId>
            <artifactId>pipelinr</artifactId>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- Spring Context (para DI) -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

### pom.xml Módulo Domain

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.empresa</groupId>
        <artifactId>reclutamiento</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>reclutamiento-domain</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- Validation API -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>

        <!-- Commons Lang (Utilities) -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

### pom.xml Módulo Infrastructure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.empresa</groupId>
        <artifactId>reclutamiento</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>reclutamiento-infrastructure</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Internal Module -->
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-domain</artifactId>
        </dependency>
        <dependency>
            <groupId>com.empresa</groupId>
            <artifactId>reclutamiento-application</artifactId>
        </dependency>

        <!-- Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- SQL Server Driver -->
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- MyBatis (opcional para queries tipo Dapper) -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>

        <!-- Flyway (migraciones) -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-sqlserver</artifactId>
        </dependency>

        <!-- Email -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mssqlserver</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

### pom.xml Módulo Common

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.empresa</groupId>
        <artifactId>reclutamiento</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>reclutamiento-common</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Utilities -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-collections4</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <!-- Documents -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
        </dependency>
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itext7-core</artifactId>
            <type>pom</type>
        </dependency>

        <!-- Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- SLF4J API (Logging) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Dependencias Gradle Completas

### build.gradle (Root)

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.6' apply false
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.empresa'
version = '1.0.0-SNAPSHOT'

ext {
    javaVersion = JavaVersion.VERSION_21
    springCloudVersion = '2023.0.3'
    pipelinrVersion = '0.8'
    mapstructVersion = '1.6.3'
    lombokMapstructBindingVersion = '0.2.0'
    lombokVersion = '1.18.36'
    jjwtVersion = '0.12.6'
    springdocVersion = '2.7.0'
    commonsLang3Version = '3.17.0'
    commonsCollections4Version = '4.5.0-M3'
    guavaVersion = '33.3.1-jre'
    poiVersion = '5.3.0'
    itextVersion = '8.0.5'
    sentryVersion = '8.0.0'
    caffeineVersion = '3.1.8'
    testcontainersVersion = '1.20.4'
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'io.spring.dependency-management'

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    dependencyManagement {
        imports {
            mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
            mavenBom "org.testcontainers:testcontainers-bom:${testcontainersVersion}"
        }
    }

    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }

    tasks.withType(JavaCompile) {
        options.encoding = 'UTF-8'
        options.compilerArgs << '-parameters'
    }

    test {
        useJUnitPlatform()
    }
}
```

### build.gradle (Módulo API)

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    // Internal Modules
    implementation project(':reclutamiento-application')
    implementation project(':reclutamiento-infrastructure')
    implementation project(':reclutamiento-common')

    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-cache'

    // OpenAPI/Swagger
    implementation "org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVersion}"

    // JWT
    implementation "io.jsonwebtoken:jjwt-api:${jjwtVersion}"
    runtimeOnly "io.jsonwebtoken:jjwt-impl:${jjwtVersion}"
    runtimeOnly "io.jsonwebtoken:jjwt-jackson:${jjwtVersion}"

    // Sentry
    implementation "io.sentry:sentry-spring-boot-starter-jakarta:${sentryVersion}"

    // Cache
    implementation "com.github.ben-manes.caffeine:caffeine:${caffeineVersion}"

    // Micrometer
    implementation 'io.micrometer:micrometer-registry-prometheus'

    // Lombok
    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

---

## Configuración de Annotation Processors

### Para Maven

Configuración crítica en `maven-compiler-plugin`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <source>21</source>
        <target>21</target>
        <annotationProcessorPaths>
            <!-- IMPORTANTE: Lombok debe ir PRIMERO -->
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <!-- MapStruct debe ir DESPUÉS de Lombok -->
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </path>
            <!-- Binding necesario para Lombok + MapStruct -->
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>${lombok-mapstruct-binding.version}</version>
            </path>
        </annotationProcessorPaths>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### Para Gradle

```gradle
dependencies {
    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    compileOnly "org.mapstruct:mapstruct:${mapstructVersion}"

    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.mapstruct:mapstruct-processor:${mapstructVersion}"
    annotationProcessor "org.projectlombok:lombok-mapstruct-binding:${lombokMapstructBindingVersion}"
}
```

---

## Estructura de Proyecto Multi-módulo

```
reclutamiento/
├── pom.xml (parent)
├── README.md
├── .gitignore
│
├── reclutamiento-domain/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/empresa/reclutamiento/domain/
│       │   ├── entities/
│       │   ├── valueobjects/
│       │   ├── repositories/ (interfaces)
│       │   ├── exceptions/
│       │   └── events/
│       └── test/
│
├── reclutamiento-application/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/empresa/reclutamiento/application/
│       │   ├── commands/
│       │   │   ├── handlers/
│       │   │   └── validators/
│       │   ├── queries/
│       │   │   ├── handlers/
│       │   │   └── validators/
│       │   ├── dto/
│       │   ├── mappers/
│       │   └── services/
│       └── test/
│
├── reclutamiento-infrastructure/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/empresa/reclutamiento/infrastructure/
│       │   ├── persistence/
│       │   │   ├── entities/
│       │   │   ├── repositories/
│       │   │   └── config/
│       │   ├── messaging/
│       │   ├── security/
│       │   └── external/
│       └── test/
│
├── reclutamiento-api/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/empresa/reclutamiento/api/
│       │   │   ├── ReclutamientoApplication.java
│       │   │   ├── controllers/
│       │   │   ├── config/
│       │   │   ├── security/
│       │   │   ├── filters/
│       │   │   └── exceptions/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-{profile}.yml
│       └── test/
│
└── reclutamiento-common/
    ├── pom.xml
    └── src/
        ├── main/java/com/empresa/reclutamiento/common/
        │   ├── constants/
        │   ├── utils/
        │   ├── exceptions/
        │   └── annotations/
        └── test/
```

---

## Configuración de Plugins

### Plugin de Spring Boot

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
        <image>
            <name>empresa/${project.artifactId}:${project.version}</name>
        </image>
    </configuration>
</plugin>
```

### Plugin de Versiones

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>2.17.1</version>
</plugin>
```

### Plugin de Code Coverage (JaCoCo)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Consideraciones de Arquitectura

### 1. CQRS con PipelinR

**En .NET usas MediatR, en Java usarás PipelinR:**

```java
// Command
public record CreateSolicitudCommand(CreateSolicitudDto dto)
    implements Command<ResponseDto<SolicitudDto>> {
}

// Command Handler
@Component
public class CreateSolicitudHandler
    implements Command.Handler<CreateSolicitudCommand, ResponseDto<SolicitudDto>> {

    @Override
    public ResponseDto<SolicitudDto> handle(CreateSolicitudCommand command) {
        // lógica del handler
    }
}

// En el controller
@PostMapping
public ResponseDto<SolicitudDto> create(@RequestBody CreateSolicitudDto dto) {
    return pipeline.send(new CreateSolicitudCommand(dto));
}
```

### 2. Auditoría Automática con Spring Data JPA

**Equivalente a tu SystemEntity en .NET:**

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable {

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 64)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 64)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Version
    @Column(name = "row_version")
    private Long rowVersion; // Control de concurrencia optimista
}
```

**Configuración:**

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

### 3. MapStruct para Mapeo

**Equivalente a AutoMapper:**

```java
@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Solicitud toEntity(CreateSolicitudDto dto);

    SolicitudDto toDto(Solicitud entity);

    List<SolicitudDto> toDtoList(List<Solicitud> entities);
}
```

### 4. Validación

**Equivalente a FluentValidation:**

```java
public record CreateSolicitudDto(
    @NotNull(message = "El área es requerida")
    Long idArea,

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String nombre,

    @Email(message = "Email inválido")
    String email,

    @Min(value = 1, message = "La prioridad debe ser mayor a 0")
    Integer prioridad
) {}
```

### 5. Manejo de Excepciones Global

**Equivalente a tu CustomExceptionHandler:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDto(
                UUID.randomUUID().toString(),
                404,
                "Not Found",
                ex.getMessage(),
                null
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        // Manejo de errores de validación
    }
}
```

---

## Resumen de Versiones Finales

| Librería | Versión | Propósito |
|----------|---------|-----------|
| Java | 21 LTS | Lenguaje base |
| Spring Boot | 3.3.6 | Framework principal |
| Spring Cloud | 2023.0.3 | Ecosistema cloud |
| PipelinR | 0.8 | CQRS/Mediator |
| MapStruct | 1.6.3 | Mapeo de objetos |
| Lombok | 1.18.36 | Reducción de boilerplate |
| jjwt | 0.12.6 | JWT |
| springdoc-openapi | 2.7.0 | OpenAPI/Swagger |
| Hibernate Validator | 8.0.1 | Validación (con Spring Boot) |
| Sentry | 8.0.0 | Monitoreo de errores |
| Caffeine | 3.1.8 | Cache |
| Commons Lang3 | 3.17.0 | Utilidades |
| Apache POI | 5.3.0 | Word/Excel |
| iText | 8.0.5 | PDF |
| Testcontainers | 1.20.4 | Testing con containers |
| MyBatis | 3.0.3 | Micro-ORM (opcional) |

---

## Próximos Pasos

1. **Crear estructura multi-módulo Maven**
2. **Configurar annotation processors (Lombok + MapStruct)**
3. **Implementar entidades base con auditoría**
4. **Configurar PipelinR para CQRS**
5. **Crear mappers con MapStruct**
6. **Implementar seguridad con JWT**
7. **Configurar Swagger/OpenAPI**
8. **Implementar handlers de Commands y Queries**

---

**Versión del Documento:** 1.0
**Fecha:** 2025-01-04
**Autor:** Equipo de Arquitectura

---

¿Listo para comenzar la implementación en Java?
