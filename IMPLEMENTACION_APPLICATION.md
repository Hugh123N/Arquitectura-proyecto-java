# Implementación de la Capa Application (Servicios de Aplicación)

## Fecha: 2025-11-06

## Índice

1. [Resumen](#resumen)
2. [Arquitectura de la Capa Application](#arquitectura-de-la-capa-application)
3. [Estructura de Módulos](#estructura-de-módulos)
4. [Comparación .NET vs Java](#comparación-net-vs-java)
5. [Clases Base](#clases-base)
6. [Interfaces de Servicios](#interfaces-de-servicios)
7. [Implementación de Servicios](#implementación-de-servicios)
8. [Controllers (API Layer)](#controllers-api-layer)
9. [Flujo de Requests](#flujo-de-requests)
10. [Configuración de Dependencias](#configuración-de-dependencias)
11. [Ejemplo Completo: Area](#ejemplo-completo-area)
12. [Buenas Prácticas](#buenas-prácticas)
13. [DO's y DON'Ts](#dos-y-donts)

---

## Resumen

La **Capa Application** actúa como una fachada (facade) entre la **Capa API** (Controllers) y la **Capa Domain** (CQRS Commands/Queries). Esta capa NO contiene lógica de negocio, solo orquestación de llamadas a través del mediador PipelinR.

### Propósito

- **Desacoplamiento**: Los controllers no conocen directamente los Commands/Queries
- **Simplificación**: Los controllers solo inyectan un servicio de aplicación en lugar de múltiples handlers
- **Consistencia**: Interfaz unificada para todas las operaciones de una entidad
- **Testabilidad**: Fácil de mockear en tests de controllers

### Módulos Creados

1. **reclutamiento-application-abstractions**: Contiene interfaces de servicios
2. **reclutamiento-application**: Contiene implementaciones de servicios

---

## Arquitectura de la Capa Application

### Vista General

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA API                                 │
│          Controllers (REST Endpoints)                       │
│                                                             │
│  AreaController implements IAreaApplication                 │
│  ├── @PostMapping → create()                               │
│  ├── @PutMapping → update()                                │
│  ├── @DeleteMapping → delete()                             │
│  ├── @GetMapping → get()                                   │
│  └── @PostMapping("/search") → search()                    │
└────────────────┬────────────────────────────────────────────┘
                 │ Dependency Injection
                 ↓
┌─────────────────────────────────────────────────────────────┐
│              CAPA APPLICATION                               │
│        Application Services (Facades)                       │
│                                                             │
│  AreaApplication extends ApplicationBase                    │
│  implements IAreaApplication                                │
│  ├── create() → mediator.send(CreateAreaCommand)           │
│  ├── update() → mediator.send(UpdateAreaCommand)           │
│  ├── delete() → mediator.send(DeleteAreaCommand)           │
│  ├── get() → mediator.send(GetAreaQuery)                   │
│  └── search() → mediator.send(SearchAreaQuery)             │
└────────────────┬────────────────────────────────────────────┘
                 │ PipelinR Mediator
                 ↓
┌─────────────────────────────────────────────────────────────┐
│               CAPA DOMAIN                                   │
│     CQRS Commands & Queries Handlers                       │
│                                                             │
│  CreateAreaCommandHandler                                   │
│  UpdateAreaCommandHandler                                   │
│  DeleteAreaCommandHandler                                   │
│  GetAreaQueryHandler                                        │
│  SearchAreaQueryHandler                                     │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de una Request

```
1. HTTP Request (POST /api/area)
   ↓
2. AreaController.create(CreateAreaDto)
   ↓
3. areaApplication.create(createDto)  ← Dependency Injection
   ↓
4. mediator.send(new CreateAreaCommand(createDto))  ← PipelinR
   ↓
5. CreateAreaCommandHandler.handle(command)
   ↓
6. Validator valida el comando
   ↓
7. Handler ejecuta lógica de negocio
   ↓
8. Repository guarda en BD
   ↓
9. Mapper convierte Entity → DTO
   ↓
10. ResponseDto<GetAreaDto>
    ↓
11. HTTP Response (JSON)
```

---

## Estructura de Módulos

### Módulo: reclutamiento-application-abstractions

**Descripción**: Contiene SOLO interfaces de servicios de aplicación.

**Estructura de Carpetas**:

```
reclutamiento-application-abstractions/
└── src/main/java/
    └── com/soft/reclutamiento/application/abstractions/
        └── dbo/
            ├── IAreaApplication.java
            ├── IEmpresaApplication.java
            ├── ITurnoApplication.java
            └── ... (Una interfaz por entidad)
```

**Dependencias** (pom.xml):

```xml
<dependencies>
    <!-- Solo DTO - NO depende de Domain ni Infrastructure -->
    <dependency>
        <groupId>com.soft</groupId>
        <artifactId>reclutamiento-dto</artifactId>
    </dependency>
</dependencies>
```

**Propósito**: Definir contratos que la API puede usar sin depender de implementaciones.

---

### Módulo: reclutamiento-application

**Descripción**: Contiene implementaciones de servicios de aplicación.

**Estructura de Carpetas**:

```
reclutamiento-application/
└── src/main/java/
    └── com/soft/reclutamiento/application/
        ├── base/
        │   └── ApplicationBase.java        ← Clase base para servicios
        └── dbo/
            ├── AreaApplication.java         ← Implementación
            ├── EmpresaApplication.java
            ├── TurnoApplication.java
            └── ... (Una implementación por entidad)
```

**Dependencias** (pom.xml):

```xml
<dependencies>
    <!-- Application Abstractions (interfaces) -->
    <dependency>
        <groupId>com.soft</groupId>
        <artifactId>reclutamiento-application-abstractions</artifactId>
    </dependency>

    <!-- Domain (Commands & Queries) -->
    <dependency>
        <groupId>com.soft</groupId>
        <artifactId>reclutamiento-domain</artifactId>
    </dependency>

    <!-- DTO -->
    <dependency>
        <groupId>com.soft</groupId>
        <artifactId>reclutamiento-dto</artifactId>
    </dependency>

    <!-- PipelinR (Mediator) -->
    <dependency>
        <groupId>net.sizovs</groupId>
        <artifactId>pipelinr</artifactId>
    </dependency>

    <!-- Spring Context (for @Service) -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
</dependencies>
```

---

## Comparación .NET vs Java

### Comparación de Arquitectura

| Aspecto | .NET (Reclutamiento) | Java (spring-base) |
|---------|----------------------|--------------------|
| **Módulo de Interfaces** | `Reclutamiento.Application.Abstractions` | `reclutamiento-application-abstractions` |
| **Módulo de Implementaciones** | `Reclutamiento.Application` | `reclutamiento-application` |
| **Clase Base** | `ApplicationBase` | `ApplicationBase` |
| **Mediator** | `MediatR` (IMediator) | `PipelinR` (Pipeline) |
| **Registro DI** | `IServiceCollection.AddScoped<I, T>()` | `@Service` + Spring DI |
| **Patrón** | Facade + Dependency Injection | Facade + Dependency Injection |

### Comparación de Código

**Interfaz (.NET vs Java)**

**.NET (C#)**:
```csharp
public interface IAreaApplication
{
    Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto);
    Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto);
    Task<ResponseDto> Delete(int id);
    Task<ResponseDto<GetAreaDto>> Get(int id);
    Task<ResponseDto<SearchResultDto<SearchAreaDto>>> Search(
        SearchParamsDto<SearchAreaFilterDto> searchParams);
}
```

**Java**:
```java
public interface IAreaApplication {
    ResponseDto<GetAreaDto> create(CreateAreaDto createDto);
    ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto);
    ResponseDto delete(Integer id);
    ResponseDto<GetAreaDto> get(Integer id);
    ResponseDto<SearchResultDto<SearchAreaDto>> search(
        SearchParamsDto<SearchAreaFilterDto> searchParams);
}
```

**Clase Base (.NET vs Java)**

**.NET (C#)**:
```csharp
public class ApplicationBase
{
    protected readonly IMapper? _mapper;
    protected readonly IMediator _mediator;
    protected readonly IUnitOfWork? _unitOfWork;

    public ApplicationBase(IMediator mediator)
        => _mediator = mediator;

    public ApplicationBase(IMediator mediator, IMapper mapper)
        : this(mediator)
        => _mapper = mapper;

    public ApplicationBase(IMediator mediator, IMapper mapper, IUnitOfWork unitOfWork)
        : this(mediator, mapper)
        => _unitOfWork = unitOfWork;
}
```

**Java**:
```java
@RequiredArgsConstructor
public abstract class ApplicationBase {
    protected final Pipeline mediator;  // PipelinR

    public ApplicationBase(Pipeline mediator) {
        this.mediator = mediator;
    }
}
```

**Servicio (.NET vs Java)**

**.NET (C#)**:
```csharp
public class AreaApplication : ApplicationBase, IAreaApplication
{
    public AreaApplication(IMediator mediator) : base(mediator) { }

    public async Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto)
        => await _mediator.Send(new CreateAreaCommand(createDto));

    public async Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto)
        => await _mediator.Send(new UpdateAreaCommand(updateDto));

    public async Task<ResponseDto> Delete(int id)
        => await _mediator.Send(new DeleteAreaCommand(id));

    public async Task<ResponseDto<GetAreaDto>> Get(int id)
        => await _mediator.Send(new GetAreaQuery(id));
}
```

**Java**:
```java
@Service
public class AreaApplication extends ApplicationBase implements IAreaApplication {

    public AreaApplication(Pipeline mediator) {
        super(mediator);
    }

    @Override
    public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
        return mediator.send(new CreateAreaCommand(createDto));
    }

    @Override
    public ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto) {
        return mediator.send(new UpdateAreaCommand(updateDto));
    }

    @Override
    public ResponseDto delete(Integer id) {
        return mediator.send(new DeleteAreaCommand(id));
    }

    @Override
    public ResponseDto<GetAreaDto> get(Integer id) {
        return mediator.send(new GetAreaQuery(id));
    }
}
```

**Controller (.NET vs Java)**

**.NET (C#)**:
```csharp
[ApiController]
[Route("api/Area")]
public class AreaController : IAreaApplication
{
    private readonly IAreaApplication _areaApplication;

    public AreaController(IAreaApplication areaApplication)
        => _areaApplication = areaApplication;

    [HttpPost]
    public async Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto)
        => await _areaApplication.Create(createDto);

    [HttpPut]
    public async Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto)
        => await _areaApplication.Update(updateDto);

    [HttpDelete("{id}")]
    public async Task<ResponseDto> Delete(int id)
        => await _areaApplication.Delete(id);

    [HttpGet("{id}")]
    public async Task<ResponseDto<GetAreaDto>> Get(int id)
        => await _areaApplication.Get(id);
}
```

**Java**:
```java
@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController implements IAreaApplication {

    private final IAreaApplication areaApplication;

    @PostMapping
    @Override
    public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
        return areaApplication.create(createDto);
    }

    @PutMapping
    @Override
    public ResponseDto<GetAreaDto> update(@RequestBody UpdateAreaDto updateDto) {
        return areaApplication.update(updateDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseDto delete(@PathVariable Integer id) {
        return areaApplication.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseDto<GetAreaDto> get(@PathVariable Integer id) {
        return areaApplication.get(id);
    }
}
```

---

## Clases Base

### ApplicationBase.java

**Ubicación**: `reclutamiento-application/src/main/java/com/soft/reclutamiento/application/base/ApplicationBase.java`

**Código Completo**:

```java
package com.soft.reclutamiento.application.base;

import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;

/**
 * Base class for all Application Services
 *
 * .NET Equivalent: Reclutamiento.Application.Base.ApplicationBase
 */
@RequiredArgsConstructor
public abstract class ApplicationBase {

    /**
     * PipelinR mediator for CQRS pattern
     * Equivalent to MediatR in .NET
     */
    protected final Pipeline mediator;

    public ApplicationBase(Pipeline mediator) {
        this.mediator = mediator;
    }
}
```

**Características**:

- ✅ **Clase abstracta**: No se puede instanciar directamente
- ✅ **Protected mediator**: Accesible para clases hijas
- ✅ **Constructor injection**: Recibe Pipeline (PipelinR)
- ✅ **@RequiredArgsConstructor**: Lombok genera constructor

**Uso en Servicios**:

```java
@Service
public class AreaApplication extends ApplicationBase implements IAreaApplication {

    public AreaApplication(Pipeline mediator) {
        super(mediator);  // Llama al constructor de ApplicationBase
    }

    // Métodos pueden usar 'mediator' directamente
    @Override
    public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
        return mediator.send(new CreateAreaCommand(createDto));
    }
}
```

---

## Interfaces de Servicios

### IAreaApplication.java

**Ubicación**: `reclutamiento-application-abstractions/src/main/java/com/soft/reclutamiento/application/abstractions/dbo/IAreaApplication.java`

**Código Completo**:

```java
package com.soft.reclutamiento.application.abstractions.dbo;

import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;

import java.util.List;

/**
 * Application Service Interface for Area entity
 *
 * .NET Equivalent: Reclutamiento.Application.Abstractions.Dbo.IAreaApplication
 */
public interface IAreaApplication {

    // CRUD Operations
    ResponseDto<GetAreaDto> create(CreateAreaDto createDto);
    ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto);
    ResponseDto delete(Integer id);
    ResponseDto<GetAreaDto> get(Integer id);

    // List/Search Operations
    ResponseDto<List<ListAreaDto>> list();
    ResponseDto<SearchResultDto<SearchAreaDto>> search(
        SearchParamsDto<SearchAreaFilterDto> searchParams);

    // Selection Operations (for dropdowns/combos)
    ResponseDto<List<SelectComboAreaDto>> selectCombo();
    ResponseDto<SearchResultDto<SelectAreaDto>> select(
        SearchParamsDto<SelectAreaFilterDto> searchParams);
}
```

**Métodos Estándar por Entidad**:

| Método | Parámetro | Retorno | Propósito |
|--------|-----------|---------|-----------|
| `create()` | `CreateXDto` | `ResponseDto<GetXDto>` | Crear nueva entidad |
| `update()` | `UpdateXDto` | `ResponseDto<GetXDto>` | Actualizar entidad existente |
| `delete()` | `Integer id` | `ResponseDto` | Eliminar entidad (soft delete) |
| `get()` | `Integer id` | `ResponseDto<GetXDto>` | Obtener por ID |
| `list()` | - | `ResponseDto<List<ListXDto>>` | Listar todas activas |
| `search()` | `SearchParamsDto<FilterDto>` | `ResponseDto<SearchResultDto<SearchXDto>>` | Búsqueda con filtros y paginación |
| `selectCombo()` | - | `ResponseDto<List<SelectComboXDto>>` | Lista simple para combos |
| `select()` | `SearchParamsDto<FilterDto>` | `ResponseDto<SearchResultDto<SelectXDto>>` | Búsqueda para selección |

---

## Implementación de Servicios

### AreaApplication.java

**Ubicación**: `reclutamiento-application/src/main/java/com/soft/reclutamiento/application/dbo/AreaApplication.java`

**Código Completo**:

```java
package com.soft.reclutamiento.application.dbo;

import an.awesome.pipelinr.Pipeline;
import com.soft.reclutamiento.application.abstractions.dbo.IAreaApplication;
import com.soft.reclutamiento.application.base.ApplicationBase;
import com.soft.reclutamiento.entity.commands.dbo.area.*;
import com.soft.reclutamiento.entity.queries.dbo.area.*;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for Area entity
 *
 * .NET Equivalent: Reclutamiento.Application.Dbo.AreaApplication
 */
@Service
public class AreaApplication extends ApplicationBase implements IAreaApplication {

    public AreaApplication(Pipeline mediator) {
        super(mediator);
    }

    @Override
    public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
        return mediator.send(new CreateAreaCommand(createDto));
    }

    @Override
    public ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto) {
        return mediator.send(new UpdateAreaCommand(updateDto));
    }

    @Override
    public ResponseDto delete(Integer id) {
        return mediator.send(new DeleteAreaCommand(id));
    }

    @Override
    public ResponseDto<GetAreaDto> get(Integer id) {
        return mediator.send(new GetAreaQuery(id));
    }

    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return mediator.send(new ListAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(
            SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return mediator.send(new SearchAreaQuery(searchParams));
    }

    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return mediator.send(new SelectComboAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(
            SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return mediator.send(new SelectAreaQuery(searchParams));
    }
}
```

**Características**:

- ✅ **@Service**: Registrado automáticamente en Spring DI
- ✅ **Extends ApplicationBase**: Hereda acceso al mediator
- ✅ **Implements IAreaApplication**: Implementa contrato
- ✅ **NO lógica de negocio**: Solo delegación a Commands/Queries
- ✅ **Constructor injection**: Recibe Pipeline de Spring

---

## Controllers (API Layer)

### AreaController.java

**Ubicación**: `reclutamiento-api/src/main/java/com/soft/reclutamiento/api/controllers/dbo/AreaController.java`

**Código Completo**:

```java
package com.soft.reclutamiento.api.controllers.dbo;

import com.soft.reclutamiento.application.abstractions.dbo.IAreaApplication;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Area entity
 *
 * .NET Equivalent: Reclutamiento.Apis.Controllers.Dbo.AreaController
 */
@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController implements IAreaApplication {

    private final IAreaApplication areaApplication;

    @PostMapping
    @Override
    public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
        return areaApplication.create(createDto);
    }

    @PutMapping
    @Override
    public ResponseDto<GetAreaDto> update(@RequestBody UpdateAreaDto updateDto) {
        return areaApplication.update(updateDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseDto delete(@PathVariable Integer id) {
        return areaApplication.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseDto<GetAreaDto> get(@PathVariable Integer id) {
        return areaApplication.get(id);
    }

    @GetMapping("/list")
    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return areaApplication.list();
    }

    @PostMapping("/search")
    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(
            @RequestBody SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return areaApplication.search(searchParams);
    }

    @GetMapping("/selectcombo")
    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return areaApplication.selectCombo();
    }

    @PostMapping("/select")
    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(
            @RequestBody SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return areaApplication.select(searchParams);
    }
}
```

**Anotaciones Spring**:

| Anotación | Propósito |
|-----------|-----------|
| `@RestController` | Marca la clase como controller REST |
| `@RequestMapping("/api/area")` | Prefijo de ruta base |
| `@RequiredArgsConstructor` | Constructor con inyección de dependencias (Lombok) |
| `@PostMapping` | Mapea HTTP POST |
| `@PutMapping` | Mapea HTTP PUT |
| `@DeleteMapping("/{id}")` | Mapea HTTP DELETE con path variable |
| `@GetMapping("/{id}")` | Mapea HTTP GET con path variable |
| `@RequestBody` | Deserializa JSON del body a DTO |
| `@PathVariable` | Extrae variable de la URL |

**Endpoints Generados**:

| Método HTTP | Ruta | Acción |
|-------------|------|--------|
| POST | `/api/area` | Crear |
| PUT | `/api/area` | Actualizar |
| DELETE | `/api/area/{id}` | Eliminar |
| GET | `/api/area/{id}` | Obtener por ID |
| GET | `/api/area/list` | Listar todas |
| POST | `/api/area/search` | Buscar con filtros |
| GET | `/api/area/selectcombo` | Combo simple |
| POST | `/api/area/select` | Buscar para selección |

---

## Flujo de Requests

### Flujo Completo: Crear Area

```
1. HTTP POST /api/area
   Body: {"nombre": "Finanzas", "descripcion": "Área de finanzas"}
   ↓
2. AreaController.create(createDto)
   - Spring deserializa JSON → CreateAreaDto
   - @RequestBody CreateAreaDto createDto
   ↓
3. areaApplication.create(createDto)
   - Controller llama al servicio inyectado
   - IAreaApplication areaApplication (inyección)
   ↓
4. mediator.send(new CreateAreaCommand(createDto))
   - AreaApplication envía comando a PipelinR
   - Pipeline mediator
   ↓
5. CreateAreaCommandHandler.handle(command)
   - PipelinR enruta al handler correspondiente
   - CommandHandlerBaseWithResponse.handle()
   ↓
6. Validator.validate(command)
   - CreateAreaCommandValidator valida
   - Valida nombre requerido, longitud, etc.
   ↓
7. handleCommand(command) - Lógica de negocio
   - Mapea DTO → Entity (MapStruct)
   - area = areaRepository.save(area)
   ↓
8. Mapea Entity → DTO
   - GetAreaDto areaDto = areaMapper.toGetAreaDto(area)
   ↓
9. return ResponseDto<GetAreaDto>
   - response.updateData(areaDto)
   - response.addOkResult("Creado exitosamente")
   ↓
10. Spring serializa ResponseDto → JSON
    ↓
11. HTTP 200 OK
    Body: {
      "isValid": true,
      "messages": ["El registro fue creado correctamente"],
      "data": {
        "id": 1,
        "nombre": "Finanzas",
        "descripcion": "Área de finanzas",
        ...
      }
    }
```

### Flujo: Búsqueda con Filtros

```
1. HTTP POST /api/area/search
   Body: {
     "filter": {"nombre": "Fin"},
     "page": {"page": 1, "pageSize": 20},
     "sort": {"property": "nombre", "direction": "asc"}
   }
   ↓
2. AreaController.search(searchParams)
   - Spring deserializa → SearchParamsDto<SearchAreaFilterDto>
   ↓
3. areaApplication.search(searchParams)
   ↓
4. mediator.send(new SearchAreaQuery(searchParams))
   ↓
5. SearchAreaQueryHandler.handle(query)
   - SearchQueryHandlerBase valida paginación automáticamente
   ↓
6. handleQuery(query) - Construir Specification
   - Specification<Area> spec = buildSpecification(filter)
   - spec.and(nombreContains("Fin"))
   - spec.and(activoEquals(true))
   ↓
7. Repository.findAll(spec, pageable)
   - Page<Area> page = areaRepository.findAll(spec, PageRequest.of(...))
   ↓
8. Mapear resultados
   - List<SearchAreaDto> dtos = areaMapper.toSearchAreaDtoList(items)
   ↓
9. Crear SearchResultDto
   - SearchResultDto<SearchAreaDto> result
   - result.items = dtos
   - result.total = page.getTotalElements()
   ↓
10. return ResponseDto<SearchResultDto<SearchAreaDto>>
    ↓
11. HTTP 200 OK
    Body: {
      "isValid": true,
      "messages": [],
      "data": {
        "items": [...],
        "total": 5
      }
    }
```

---

## Configuración de PipelinR

### PipelinRConfig.java

**Ubicación**: `reclutamiento-api/src/main/java/com/soft/reclutamiento/api/config/PipelinRConfig.java`

**Propósito**: Configurar PipelinR (mediador CQRS) como bean de Spring.

**Código Completo**:

```java
package com.soft.reclutamiento.api.config;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelinRConfig {

    @Bean
    public Pipeline pipeline(ObjectProvider<Command.Handler> commandHandlers) {
        return new Pipelinr()
                .with(() -> commandHandlers.stream().iterator());
    }
}
```

**Explicación**:

1. **@Configuration**: Marca la clase como configuración de Spring
2. **@Bean**: Registra Pipeline como bean en el contexto de Spring
3. **ObjectProvider<Command.Handler>**: Spring inyecta TODOS los handlers registrados
4. **Pipelinr().with()**: Configura PipelinR con todos los handlers disponibles

**¿Por qué es necesario?**

- PipelinR no tiene auto-configuración de Spring Boot
- Necesitamos registrar manualmente el bean Pipeline
- Spring inyectará automáticamente este bean en ApplicationBase

### application.yml

**Ubicación**: `reclutamiento-api/src/main/resources/application.yml`

**Configuración Básica**:

```yaml
spring:
  application:
    name: reclutamiento-api

  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=SolicitudLaboratorio;encrypt=false
    username: sa
    password: YourPassword
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true

server:
  port: 8080

logging:
  level:
    com.soft.reclutamiento: DEBUG
```

---

## Configuración de Dependencias

### Parent pom.xml

**Módulos agregados**:

```xml
<modules>
    <module>reclutamiento-entity</module>
    <module>reclutamiento-dto</module>
    <module>reclutamiento-domain</module>
    <module>reclutamiento-infrastructure</module>
    <module>reclutamiento-application-abstractions</module>  ← NUEVO
    <module>reclutamiento-application</module>              ← NUEVO
    <module>reclutamiento-api</module>
</modules>
```

**DependencyManagement agregado**:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.soft</groupId>
            <artifactId>reclutamiento-application-abstractions</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.soft</groupId>
            <artifactId>reclutamiento-application</artifactId>
            <version>${project.version}</version>
        </dependency>[ACTUALIZACION_JAVA21_RESUMEN.md](../../../hugo-proyects/mcp-Java-21/ACTUALIZACION_JAVA21_RESUMEN.md)
    </dependencies>
</dependencyManagement>
```

### reclutamiento-api/pom.xml

**Cambio de dependencia**:

```xml
<!-- ANTES: API dependía directamente de Domain -->
<dependency>
    <groupId>com.soft</groupId>
    <artifactId>reclutamiento-domain</artifactId>
</dependency>

<!-- DESPUÉS: API depende de Application (que internamente usa Domain) -->
<dependency>
    <groupId>com.soft</groupId>
    <artifactId>reclutamiento-application</artifactId>
</dependency>
```

**Beneficio**: API no conoce directamente Commands/Queries, solo servicios.

---

## Ejemplo Completo: Area

### 1. Interfaz (reclutamiento-application-abstractions)

**Archivo**: `IAreaApplication.java`

```java
public interface IAreaApplication {
    ResponseDto<GetAreaDto> create(CreateAreaDto createDto);
    ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto);
    ResponseDto delete(Integer id);
    ResponseDto<GetAreaDto> get(Integer id);
    ResponseDto<List<ListAreaDto>> list();
    ResponseDto<SearchResultDto<SearchAreaDto>> search(
        SearchParamsDto<SearchAreaFilterDto> searchParams);
    ResponseDto<List<SelectComboAreaDto>> selectCombo();
    ResponseDto<SearchResultDto<SelectAreaDto>> select(
        SearchParamsDto<SelectAreaFilterDto> searchParams);
}
```

### 2. Implementación (reclutamiento-application)

**Archivo**: `AreaApplication.java`

```java
@Service
public class AreaApplication extends ApplicationBase implements IAreaApplication {

    public AreaApplication(Pipeline mediator) {
        super(mediator);
    }

    @Override
    public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
        return mediator.send(new CreateAreaCommand(createDto));
    }

    @Override
    public ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto) {
        return mediator.send(new UpdateAreaCommand(updateDto));
    }

    @Override
    public ResponseDto delete(Integer id) {
        return mediator.send(new DeleteAreaCommand(id));
    }

    @Override
    public ResponseDto<GetAreaDto> get(Integer id) {
        return mediator.send(new GetAreaQuery(id));
    }

    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return mediator.send(new ListAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(
            SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return mediator.send(new SearchAreaQuery(searchParams));
    }

    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return mediator.send(new SelectComboAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(
            SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return mediator.send(new SelectAreaQuery(searchParams));
    }
}
```

### 3. Controller (reclutamiento-api)

**Archivo**: `AreaController.java`

```java
@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController implements IAreaApplication {

    private final IAreaApplication areaApplication;

    @PostMapping
    @Override
    public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
        return areaApplication.create(createDto);
    }

    @PutMapping
    @Override
    public ResponseDto<GetAreaDto> update(@RequestBody UpdateAreaDto updateDto) {
        return areaApplication.update(updateDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseDto delete(@PathVariable Integer id) {
        return areaApplication.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseDto<GetAreaDto> get(@PathVariable Integer id) {
        return areaApplication.get(id);
    }

    @GetMapping("/list")
    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return areaApplication.list();
    }

    @PostMapping("/search")
    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(
            @RequestBody SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return areaApplication.search(searchParams);
    }

    @GetMapping("/selectcombo")
    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return areaApplication.selectCombo();
    }

    @PostMapping("/select")
    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(
            @RequestBody SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return areaApplication.select(searchParams);
    }
}
```

---

## Buenas Prácticas

### ✅ DO's

1. **Mantener Application Services SIN lógica de negocio**
   ```java
   // ✅ CORRECTO
   @Override
   public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
       return mediator.send(new CreateAreaCommand(createDto));
   }

   // ❌ INCORRECTO - No poner lógica aquí
   @Override
   public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
       if (createDto.getNombre() == null) {
           return ResponseDto.error("Nombre requerido");  // ❌ Validación aquí
       }
       Area area = new Area();  // ❌ Mapeo manual aquí
       area.setNombre(createDto.getNombre());
       return mediator.send(new CreateAreaCommand(createDto));
   }
   ```

2. **Usar Dependency Injection para servicios**
   ```java
   // ✅ CORRECTO
   @Service
   public class AreaApplication extends ApplicationBase {
       public AreaApplication(Pipeline mediator) {  // Constructor injection
           super(mediator);
       }
   }

   // ❌ INCORRECTO
   public class AreaApplication {
       private Pipeline mediator = new Pipeline();  // ❌ No instanciar manualmente
   }
   ```

3. **Implementar siempre la interfaz**
   ```java
   // ✅ CORRECTO
   @Service
   public class AreaApplication extends ApplicationBase implements IAreaApplication {
       // Implementa todos los métodos de la interfaz
   }

   // ❌ INCORRECTO
   @Service
   public class AreaApplication extends ApplicationBase {
       // Sin implementar interfaz
   }
   ```

4. **Controllers solo deben delegar**
   ```java
   // ✅ CORRECTO
   @PostMapping
   public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
       return areaApplication.create(createDto);  // Solo delegación
   }

   // ❌ INCORRECTO
   @PostMapping
   public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
       if (createDto.getNombre() == null) {  // ❌ Validación en controller
           return ResponseDto.error("Nombre requerido");
       }
       return areaApplication.create(createDto);
   }
   ```

5. **Usar @RequiredArgsConstructor para controllers**
   ```java
   // ✅ CORRECTO
   @RestController
   @RequiredArgsConstructor  // Lombok genera constructor
   public class AreaController {
       private final IAreaApplication areaApplication;
   }

   // ❌ INCORRECTO
   @RestController
   public class AreaController {
       @Autowired  // ❌ Field injection
       private IAreaApplication areaApplication;
   }
   ```

### ❌ DON'Ts

1. **NO poner lógica de negocio en Application Services**
   ```java
   // ❌ INCORRECTO
   @Override
   public ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto) {
       Area area = areaRepository.findById(updateDto.getId()).orElse(null);
       if (area == null) {
           return ResponseDto.error("Area no encontrada");
       }
       area.setNombre(updateDto.getNombre());
       areaRepository.save(area);
       return ResponseDto.ok();
   }
   ```

2. **NO acceder directamente a repositorios desde Application Services**
   ```java
   // ❌ INCORRECTO
   @Service
   public class AreaApplication extends ApplicationBase {
       private final AreaRepository areaRepository;  // ❌ NO

       @Override
       public ResponseDto delete(Integer id) {
           areaRepository.deleteById(id);  // ❌ NO
           return ResponseDto.ok();
       }
   }
   ```

3. **NO crear instancias de Commands/Queries sin parámetros correctos**
   ```java
   // ❌ INCORRECTO
   @Override
   public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
       CreateAreaCommand command = new CreateAreaCommand();  // ❌ Constructor vacío
       command.setCreateDto(createDto);  // ❌ Setter
       return mediator.send(command);
   }

   // ✅ CORRECTO
   @Override
   public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
       return mediator.send(new CreateAreaCommand(createDto));  // ✅ Constructor con parámetro
   }
   ```

4. **NO hacer validaciones en Controllers**
   ```java
   // ❌ INCORRECTO
   @PostMapping
   public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
       if (createDto.getNombre() == null || createDto.getNombre().isEmpty()) {
           ResponseDto<GetAreaDto> response = new ResponseDto<>();
           response.addErrorResult("Nombre es requerido");
           return response;
       }
       return areaApplication.create(createDto);
   }

   // ✅ CORRECTO
   @PostMapping
   public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
       return areaApplication.create(createDto);  // Validación en CommandValidator
   }
   ```

5. **NO mezclar Async/Sync incorrectamente**
   ```java
   // ❌ INCORRECTO
   @Override
   public CompletableFuture<ResponseDto<GetAreaDto>> create(CreateAreaDto createDto) {
       // PipelinR en Java es síncrono, no retornar CompletableFuture sin razón
       return CompletableFuture.supplyAsync(() ->
           mediator.send(new CreateAreaCommand(createDto)));
   }

   // ✅ CORRECTO
   @Override
   public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
       return mediator.send(new CreateAreaCommand(createDto));
   }
   ```

---

## DO's y DON'Ts

### Application Services

| ✅ DO | ❌ DON'T |
|-------|----------|
| Solo delegar a Commands/Queries | Contener lógica de negocio |
| Usar constructor injection | Usar field injection (@Autowired en campos) |
| Extender ApplicationBase | Crear tu propia instancia de Pipeline |
| Implementar interfaz correspondiente | Trabajar sin interfaz |
| Usar @Service para registro automático | Registrar manualmente en @Configuration |

### Controllers

| ✅ DO | ❌ DON'T |
|-------|----------|
| Solo delegar a Application Services | Llamar directamente a Commands/Queries |
| Usar @RequiredArgsConstructor | Usar @Autowired en campos |
| Implementar interfaz de Application | Tener métodos que no están en la interfaz |
| Usar anotaciones Spring MVC correctas | Crear endpoints sin anotaciones |
| Retornar ResponseDto directamente | Envolver ResponseDto en ResponseEntity<> |

### Interfaces

| ✅ DO | ❌ DON'T |
|-------|----------|
| Definir todos los métodos CRUD | Omitir métodos estándar |
| Usar nombres consistentes (create, update, delete, get) | Usar nombres diferentes por entidad |
| Retornar siempre ResponseDto | Retornar tipos primitivos o entities |
| Seguir convenciones de nombres de DTOs | Mezclar tipos de DTOs |

---

## Conclusión

La **Capa Application** es una capa delgada de orquestación que:

✅ **Desacopla** Controllers de Domain
✅ **Simplifica** Controllers (solo inyectan un servicio)
✅ **Mantiene** lógica de negocio en Domain (Command/Query Handlers)
✅ **Facilita** testing (fácil de mockear)
✅ **Sigue** patrón Facade
✅ **Replica** arquitectura del proyecto .NET exactamente

**Estructura Final de Capas**:

```
reclutamiento-api (Controllers REST)
    ↓ usa
reclutamiento-application (Application Services - Facade)
    ↓ usa
reclutamiento-domain (CQRS Handlers)
    ↓ usa
reclutamiento-infrastructure (Repositories)
    ↓ usa
reclutamiento-entity (Entities JPA)
```

---

**Autor**: Claude Code Assistant
**Fecha**: 2025-11-06
**Versión**: 1.0
**Estado**: ✅ Completo
