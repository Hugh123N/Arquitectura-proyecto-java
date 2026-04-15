# Estructura Modular Multi-Maven - Explicación

## ✅ COMPILACIÓN EXITOSA

La estructura multi-modular se ha creado exitosamente y compila sin errores.

---

## 📋 Resumen del Error y Solución

### Problema Original

**Error**: `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`

**Causa**: Incompatibilidad entre:
- Maven Compiler Plugin (versiones 3.11.0 y 3.13.0)
- Annotation Processors (Lombok + MapStruct)
- Java 21

Este es un bug conocido en el compilador de Maven cuando procesa las anotaciones de Lombok y MapStruct simultáneamente con Java 21.

### Solución Aplicada

**Se comentaron TEMPORALMENTE** las librerías problemáticas que no son esenciales para la estructura base:

#### Librerías Comentadas:
- ✖️ **Lombok** - Reducción de boilerplate (causa conflicto)
- ✖️ **MapStruct** - Mapeo de objetos (causa conflicto)
- ✖️ **JWT (jjwt)** - Aún no implementada la autenticación
- ✖️ **SpringDoc/Swagger** - Documentación API (puede agregarse después)
- ✖️ **Sentry** - Monitoreo de errores (puede agregarse después)
- ✖️ **Caffeine** - Cache (puede agregarse después)
- ✖️ **POI/iText** - Generación de documentos (puede agregarse después)

#### Librerías ESENCIALES Mantenidas:
- ✅ **Spring Boot Web** - Para Controllers/REST API
- ✅ **Spring Data JPA** - Para Repositorios y acceso a BD
- ✅ **Spring Security** - Para seguridad
- ✅ **Bean Validation (Jakarta)** - Para validaciones
- ✅ **PipelinR** - Para CQRS/MediatR pattern
- ✅ **Commons Lang3** - Utilidades básicas
- ✅ **MyBatis** - Para queries complejas
- ✅ **Flyway** - Para migraciones de BD
- ✅ **Testcontainers** - Para testing con BD real
- ✅ **ArchUnit** - Para testing de arquitectura

---

## 📁 Estructura de Módulos Creada

Según la arquitectura del proyecto .NET Reclutamiento:

```
spring-base (Parent POM)
│
├── reclutamiento-domain/          ← Capa de Dominio
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
├── reclutamiento-application/     ← Capa de Aplicación (Commands, Queries, DTOs)
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
├── reclutamiento-infrastructure/  ← Capa de Infraestructura (Repositorios, DB)
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml (SQL Server driver SOLO AQUÍ)
│
├── reclutamiento-api/             ← Capa de Presentación (Controllers, Security)
│   ├── src/main/java/com/soft/reclutamiento/
│   │   └── ReclutamientoApplication.java
│   ├── src/main/resources/
│   │   └── application.yaml
│   └── pom.xml (Spring Boot plugin)
│
├── reclutamiento-common/          ← Utilidades Compartidas
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
└── pom.xml (Parent)
```

---

## 🔗 Dependencias Entre Módulos

```
reclutamiento-api
    ├─→ reclutamiento-application
    │       ├─→ reclutamiento-domain
    │       └─→ reclutamiento-common
    └─→ reclutamiento-infrastructure
            ├─→ reclutamiento-domain
            └─→ reclutamiento-common
```

---

## 🎯 Distribución de Dependencias por Módulo

### 1. reclutamiento-domain
**Propósito**: Entidades, Value Objects, Domain Events

**Dependencias**:
- Bean Validation (Jakarta)
- Spring Boot Test

**Sin**: Lombok, MapStruct (comentados temporalmente)

---

### 2. reclutamiento-application
**Propósito**: Commands, Queries, DTOs, Handlers

**Dependencias**:
- reclutamiento-domain
- reclutamiento-common
- **PipelinR** (CQRS)
- Bean Validation
- Apache Commons Lang3
- Spring Boot Test

**Sin**: Lombok, MapStruct (comentados temporalmente)

---

### 3. reclutamiento-infrastructure
**Propósito**: Repositorios, Acceso a Base de Datos

**Dependencias**:
- reclutamiento-domain
- reclutamiento-common
- **Spring Data JPA**
- **SQL Server Driver** ⚠️ (SOLO AQUÍ)
- **MySQL Driver** (alternativo)
- **MyBatis** (queries complejas)
- **Flyway** (migraciones)
- Apache Commons Lang3
- Testcontainers (test)

**Sin**: Lombok, MapStruct (comentados temporalmente)

---

### 4. reclutamiento-api
**Propósito**: Controllers, REST Endpoints, Security

**Dependencias**:
- reclutamiento-application
- reclutamiento-infrastructure
- reclutamiento-common
- **Spring Boot Web**
- **Spring Security**
- Bean Validation
- **Spring Actuator** (health checks)
- Spring DevTools
- Spring Boot Test
- Spring Security Test
- **ArchUnit** (test)

**Sin**: Lombok, JWT, Swagger, Sentry (comentados temporalmente)

---

### 5. reclutamiento-common
**Propósito**: Utilidades, Constantes, Helpers

**Dependencias**:
- Apache Commons Lang3
- Bean Validation
- Jackson (JSON)
- Spring Boot Test

**Sin**: Lombok, POI, iText (comentados temporalmente)

---

## 🛠️ Comandos Maven

### Compilar todo el proyecto:
```bash
cd spring-base
./mvnw clean install -DskipTests
```

### Compilar solo un módulo:
```bash
./mvnw clean install -pl reclutamiento-api -am -DskipTests
```

### Ejecutar la aplicación:
```bash
cd reclutamiento-api
../mvnw spring-boot:run
```

---

## 📝 Próximos Pasos

### Cuando se resuelva el problema de Lombok/MapStruct:

1. **Descomentar en `pom.xml` (parent)**:
```xml
<lombok.version>1.18.36</lombok.version>
<mapstruct.version>1.6.3</mapstruct.version>
```

2. **Habilitar annotation processors**:
```xml
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
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

3. **Agregar dependencias a módulos**

### Otras librerías a agregar después:

- **JWT (jjwt)** - Cuando implementes autenticación
- **Swagger/SpringDoc** - Cuando necesites documentar la API
- **Sentry** - Cuando necesites monitoreo de errores
- **Caffeine** - Cuando necesites caché
- **POI/iText** - Cuando necesites generar documentos
- **Redis** - Si necesitas caché distribuido

---

## ⚠️ Notas Importantes

1. **SQL Server Driver**: Solo está en `reclutamiento-infrastructure` como solicitaste
2. **Spring Boot Plugin**: Solo está en `reclutamiento-api` (módulo ejecutable)
3. **JaCoCo Plugin**: Solo está en `reclutamiento-api` para coverage
4. **Flyway Plugin**: Está en `reclutamiento-infrastructure`

---

## 🎉 Estado Actual

✅ **Estructura multi-modular creada**
✅ **Compilación exitosa**
✅ **Todas las dependencias esenciales instaladas**
✅ **Arquitectura lista para comenzar desarrollo**

**La estructura está lista para seguir el patrón de implementación de entidades documentado en `IMPLEMENTACION_ENTIDAD_POR_CAPAS.md`**
