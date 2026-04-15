# Implementación de una Entidad Completa por Capas
## Clean Architecture + CQRS - Proyecto Reclutamiento .NET

---

## Tabla de Contenidos

1. [Introducción](#introducción)
2. [Estructura General por Entidad](#estructura-general-por-entidad)
3. [Capa 1: Entity (Entidad de Base de Datos)](#capa-1-entity-entidad-de-base-de-datos)
4. [Capa 2: DTOs (Data Transfer Objects)](#capa-2-dtos-data-transfer-objects)
5. [Capa 3: Domain - Mapping (AutoMapper Profiles)](#capa-3-domain---mapping-automapper-profiles)
6. [Capa 4: Domain - Commands (Escritura CQRS)](#capa-4-domain---commands-escritura-cqrs)
7. [Capa 5: Domain - Queries (Lectura CQRS)](#capa-5-domain---queries-lectura-cqrs)
8. [Capa 6: Domain - Resources (Mensajes)](#capa-6-domain---resources-mensajes)
9. [Capa 7: Application Abstractions (Interfaces)](#capa-7-application-abstractions-interfaces)
10. [Capa 8: Application (Implementación)](#capa-8-application-implementación)
11. [Capa 9: API - Controllers](#capa-9-api---controllers)
12. [Flujo Completo de una Operación](#flujo-completo-de-una-operación)
13. [Checklist de Implementación](#checklist-de-implementación)
14. [Convenciones de Nomenclatura](#convenciones-de-nomenclatura)

---

## Introducción

Este documento detalla cómo se implementa una entidad completa en el proyecto Reclutamiento (.NET), siguiendo la arquitectura Clean Architecture + CQRS. Usaremos la entidad `Area` como ejemplo real del proyecto.

### Entidades del Sistema (25 entidades)

El proyecto tiene 25 entidades principales organizadas en carpetas `Dbo`:

1. Area
2. SubArea
3. AsignacionReclutadores
4. Categoria
5. ClienteReporte
6. DuracionContrato
7. Especialidad
8. EstadoPostulacion
9. EstadoReclutador
10. EstadoRequerimiento
11. Estudios
12. ExperienciaLaboral
13. ExperienciaReclutador
14. Gerencias
15. NivelPosicion
16. OrigenPosicion
17. Postulante
18. Reclutador
19. Requerimiento
20. RequerimientoEspecialidad
21. RequerimientoPostulante
22. Sexo
23. Solicitante
24. TipoDocumento
25. (+ otras entidades base)

### Patrón de Organización

**TODAS las entidades siguen el mismo patrón:**

```
📁 {ProyectoLayer}/Dbo/{NombreEntidad}/
    └── {Archivos específicos de la entidad}
```

---

## Estructura General por Entidad

Para cada entidad (ej: `Area`), se crean archivos en las siguientes ubicaciones:

```
Reclutamiento/
│
├── Reclutamiento.Entity/
│   └── Area.cs                                    [1 archivo]
│
├── Reclutamiento.Dto/Dbo/Area/
│   ├── AreaDto.cs                                 [Clase base con propiedades]
│   ├── CreateAreaDto.cs                           [Para crear]
│   ├── UpdateAreaDto.cs                           [Para actualizar]
│   ├── GetAreaDto.cs                              [Para obtener 1 registro]
│   ├── ListAreaDto.cs                             [Para listar]
│   ├── SearchAreaDto.cs                           [Para búsqueda avanzada]
│   ├── SearchAreaFilterDto.cs                     [Filtros de búsqueda]
│   ├── SelectAreaDto.cs                           [Para select con paginación]
│   ├── SelectAreaFilterDto.cs                     [Filtros de select]
│   ├── SelectComboAreaDto.cs                      [Para combo simple]
│   └── AreaFilterDto.cs                           [Filtros base]
│                                                   [11 archivos]
│
├── Reclutamiento.Domain/Mapping/Dbo/
│   └── AreaProfile.cs                             [AutoMapper profile]
│                                                   [1 archivo]
│
├── Reclutamiento.Domain/Commands/Dbo/Area/
│   ├── CreateAreaCommand.cs                       [Command]
│   ├── CreateAreaCommandHandler.cs                [Handler]
│   ├── CreateAreaCommandValidator.cs              [Validator]
│   ├── UpdateAreaCommand.cs                       [Command]
│   ├── UpdateAreaCommandHandler.cs                [Handler]
│   ├── UpdateAreaCommandValidator.cs              [Validator]
│   ├── DeleteAreaCommand.cs                       [Command]
│   ├── DeleteAreaCommandHandler.cs                [Handler]
│   └── DeleteAreaCommandValidator.cs              [Validator]
│                                                   [9 archivos]
│
├── Reclutamiento.Domain/Queries/Dbo/Area/
│   ├── GetAreaQuery.cs                            [Query]
│   ├── GetAreaQueryHandler.cs                     [Handler]
│   ├── GetAreaQueryValidator.cs                   [Validator]
│   ├── ListAreaQuery.cs                           [Query]
│   ├── ListAreaQueryHandler.cs                    [Handler]
│   ├── SearchAreaQuery.cs                         [Query]
│   ├── SearchAreaQueryHandler.cs                  [Handler]
│   ├── SelectAreaQuery.cs                         [Query]
│   ├── SelectAreaQueryHandler.cs                  [Handler]
│   ├── SelectComboAreaQuery.cs                    [Query]
│   └── SelectComboAreaQueryHandler.cs             [Handler]
│                                                   [11 archivos]
│
├── Reclutamiento.Domain/Resources/Dbo/
│   ├── Area.resx                                  [Recursos de mensajes]
│   └── Area.Designer.cs                           [Generado automáticamente]
│                                                   [2 archivos]
│
├── Reclutamiento.Application.Abstractions/Dbo/
│   └── IAreaApplication.cs                        [Interface]
│                                                   [1 archivo]
│
├── Reclutamiento.Application/Dbo/
│   └── AreaApplication.cs                         [Implementación]
│                                                   [1 archivo]
│
└── Reclutamiento.Apis/Controllers/Dbo/
    └── AreaController.cs                          [Controller]
                                                    [1 archivo]

TOTAL POR ENTIDAD: ~38 archivos
```

---

## Capa 1: Entity (Entidad de Base de Datos)

### Ubicación
```
Reclutamiento.Entity/Area.cs
```

### Responsabilidad
- Representación 1:1 de la tabla de base de datos
- Propiedades con tipos de datos de .NET
- Incluye campos de auditoría (estándar en todas las entidades)

### Código Real - Area.cs

```csharp
using System;
using System.Collections.Generic;

namespace Reclutamiento.Entity;

public partial class Area
{
    // Primary Key
    public int IdArea { get; set; }

    // Business Properties
    public string? Nombre { get; set; }

    // Audit Fields (Estándar en TODAS las entidades)
    public string UserNameCreate { get; set; } = null!;
    public DateTimeOffset CreateDate { get; set; }
    public string? UserNameUpdate { get; set; }
    public DateTimeOffset? UpdateDate { get; set; }
    public bool Activo { get; set; }
    public byte[] RowVersion { get; set; } = null!;  // Concurrency control
}
```

### Características Clave

1. **Campos de Auditoría** (presentes en TODAS las entidades):
   - `UserNameCreate` - Usuario que creó
   - `CreateDate` - Fecha de creación
   - `UserNameUpdate` - Usuario que modificó
   - `UpdateDate` - Fecha de modificación
   - `Activo` - Flag de soft delete
   - `RowVersion` - Control de concurrencia optimista (timestamp)

2. **Partial Class**: Permite extensiones en otro archivo

3. **Nomenclatura**:
   - PK: `Id{NombreEntidad}`
   - FK: `Id{EntidadRelacionada}`

---

## Capa 2: DTOs (Data Transfer Objects)

### Ubicación
```
Reclutamiento.Dto/Dbo/Area/
```

### Patrón de Herencia para DTOs

```
AreaDto (Base)
    ├── CreateAreaDto
    ├── UpdateAreaDto
    ├── GetAreaDto
    ├── ListAreaDto
    ├── SearchAreaDto
    └── SelectAreaDto
```

### 1. AreaDto.cs (Clase Base)

**Responsabilidad**: Contiene las propiedades de negocio comunes a todos los DTOs

```csharp
using System;
using System.Collections.Generic;

namespace Reclutamiento.Dto.Dbo.Area;

public class AreaDto
{
    public string? Nombre { get; set; }
    // Solo propiedades de negocio, NO incluir Id, Activo, campos de auditoría
}
```

### 2. CreateAreaDto.cs

**Responsabilidad**: Para crear nuevos registros

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class CreateAreaDto : AreaDto
    {
        // Hereda todas las propiedades de AreaDto
        // NO incluir Id (se genera en BD)
        // NO incluir campos de auditoría (se llenan automáticamente)
    }
}
```

### 3. UpdateAreaDto.cs

**Responsabilidad**: Para actualizar registros existentes

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class UpdateAreaDto : AreaDto
    {
        public int IdArea { get; set; }  // SE INCLUYE el Id para identificar qué actualizar
        // Hereda propiedades de negocio de AreaDto
    }
}
```

### 4. GetAreaDto.cs

**Responsabilidad**: Para obtener un solo registro completo

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class GetAreaDto : AreaDto
    {
        public int IdArea { get; set; }
        public bool Activo { get; set; }
        // Opcionalmente campos de auditoría si se necesitan mostrar
    }
}
```

### 5. ListAreaDto.cs

**Responsabilidad**: Para listar registros (sin paginación)

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class ListAreaDto : AreaDto
    {
        // Propiedades mínimas para lista
        // Hereda de AreaDto
    }
}
```

### 6. SearchAreaDto.cs

**Responsabilidad**: Para resultados de búsqueda paginada

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class SearchAreaDto : AreaDto
    {
        public int IdArea { get; set; }
        public bool Activo { get; set; }
        // Campos necesarios para mostrar en grilla de búsqueda
    }
}
```

### 7. SearchAreaFilterDto.cs

**Responsabilidad**: Filtros para búsqueda

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class SearchAreaFilterDto
    {
        public DateTimeOffset? FechaDesde { get; set; }
        public DateTimeOffset? FechaHasta { get; set; }
        public int? IdArea { get; set; }
        public bool? Activo { get; set; }
        // Todos los campos son OPCIONALES (nullable)
    }
}
```

### 8. SelectAreaDto.cs

**Responsabilidad**: Para dropdown con paginación

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class SelectAreaDto : AreaDto
    {
        public int IdArea { get; set; }
        public bool Activo { get; set; }
    }
}
```

### 9. SelectAreaFilterDto.cs

**Responsabilidad**: Filtros para dropdown con paginación

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class SelectAreaFilterDto
    {
        public DateTimeOffset? FechaDesde { get; set; }
        public DateTimeOffset? FechaHasta { get; set; }
        public int? IdArea { get; set; }
        public bool? Activo { get; set; }
    }
}
```

### 10. SelectComboAreaDto.cs

**Responsabilidad**: Para dropdown simple sin paginación

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class SelectComboAreaDto : AreaDto
    {
        public int IdArea { get; set; }
        public bool Activo { get; set; }
    }
}
```

### 11. AreaFilterDto.cs

**Responsabilidad**: Filtros base

```csharp
namespace Reclutamiento.Dto.Dbo.Area
{
    public class AreaFilterDto
    {
        public int? IdArea { get; set; }
        public bool? Activo { get; set; }
    }
}
```

---

## Capa 3: Domain - Mapping (AutoMapper Profiles)

### Ubicación
```
Reclutamiento.Domain/Mapping/Dbo/AreaProfile.cs
```

### Responsabilidad
- Configurar mapeos bidireccionales entre Entity y DTOs
- Un Profile por entidad

### Código Real - AreaProfile.cs

```csharp
using AutoMapper;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Mapping.Area
{
    public class AreaProfile : Profile
    {
        public AreaProfile()
        {
            // Mapeo base
            CreateMap<Entity.Area, AreaDto>()
                .ReverseMap();

            // Mapeo para Create
            CreateMap<Entity.Area, CreateAreaDto>()
                .ReverseMap();

            // Mapeo para Update
            CreateMap<Entity.Area, UpdateAreaDto>()
                .ReverseMap();

            // Mapeo para Get
            CreateMap<Entity.Area, GetAreaDto>()
                .ReverseMap();

            // Mapeo para List
            CreateMap<Entity.Area, ListAreaDto>()
                .ReverseMap();

            // Mapeo para SelectCombo
            CreateMap<Entity.Area, SelectComboAreaDto>()
                .ReverseMap();

            // Mapeo para Search
            CreateMap<Entity.Area, SearchAreaDto>()
                .ReverseMap();

            // NO es necesario mapear SelectAreaDto porque es igual a SearchAreaDto
        }
    }
}
```

### Patrón Estándar

**SIEMPRE incluir estos mapeos mínimos:**
1. AreaDto ↔ Entity.Area
2. CreateAreaDto ↔ Entity.Area
3. UpdateAreaDto ↔ Entity.Area
4. GetAreaDto ↔ Entity.Area
5. ListAreaDto ↔ Entity.Area
6. SelectComboAreaDto ↔ Entity.Area
7. SearchAreaDto ↔ Entity.Area

---

## Capa 4: Domain - Commands (Escritura CQRS)

### Ubicación
```
Reclutamiento.Domain/Commands/Dbo/Area/
```

### Operaciones CRUD (3 Commands)

1. **CreateAreaCommand** - Crear
2. **UpdateAreaCommand** - Actualizar
3. **DeleteAreaCommand** - Eliminar (Soft Delete)

---

### 4.1 CREATE - Crear Nuevo Registro

#### CreateAreaCommand.cs

```csharp
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class CreateAreaCommand : CommandBase<GetAreaDto>
    {
        public CreateAreaCommand(CreateAreaDto createDto)
            => CreateDto = createDto;

        public CreateAreaDto CreateDto { get; set; }
    }
}
```

**Características:**
- Hereda de `CommandBase<GetAreaDto>` (retorna GetAreaDto)
- Recibe `CreateAreaDto` en constructor
- Propiedad pública `CreateDto`

#### CreateAreaCommandHandler.cs

```csharp
using AutoMapper;
using MediatR;
using Microsoft.EntityFrameworkCore;
using Reclutamiento.Common;
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Dto.Base;
using Reclutamiento.Repository.Abstractions.Base;
using Reclutamiento.Repository.Abstractions.Transactions;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class CreateAreaCommandHandler : CommandHandlerBase<CreateAreaCommand, GetAreaDto>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public CreateAreaCommandHandler(
            IUnitOfWork unitOfWork,
            IMapper mapper,
            IMediator mediator,
            CreateAreaCommandValidator validator,
            IRepository<Entity.Area> AreaRepository
        ) : base(unitOfWork, mapper, mediator, validator)
        {
            _AreaRepository = AreaRepository;
        }

        public override async Task<ResponseDto<GetAreaDto>> HandleCommand(
            CreateAreaCommand request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<GetAreaDto>();

            // 1. Mapear DTO a Entity
            var Area = _mapper?.Map<Entity.Area>(request.CreateDto);

            if (Area != null)
            {
                // 2. Agregar al repositorio
                await _AreaRepository.AddAsync(Area);

                // 3. Guardar cambios
                await _AreaRepository.SaveAsync();
            }

            // 4. Mapear Entity a DTO de respuesta
            var AreaDto = _mapper?.Map<GetAreaDto>(Area);
            if (AreaDto != null) response.UpdateData(AreaDto);

            // 5. Agregar mensaje de éxito
            response.AddOkResult(Resources.Common.CreateSuccessMessage);

            return await Task.FromResult(response);
        }
    }
}
```

**Flujo del Handler:**
1. Crear ResponseDto
2. Mapear CreateAreaDto → Entity.Area
3. AddAsync al repositorio
4. SaveAsync para persistir
5. Mapear Entity.Area → GetAreaDto
6. Agregar mensaje de éxito
7. Retornar response

**Dependencias Inyectadas:**
- `IUnitOfWork` - Transacciones
- `IMapper` - AutoMapper
- `IMediator` - MediatR
- `CreateAreaCommandValidator` - Validador
- `IRepository<Entity.Area>` - Repositorio específico

#### CreateAreaCommandValidator.cs

```csharp
using Reclutamiento.Domain.Commands.Base;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class CreateAreaCommandValidator : CommandValidatorBase<CreateAreaCommand>
    {
        public CreateAreaCommandValidator()
        {
            RequiredInformation(x => x.CreateDto).DependentRules(() =>
            {
                // Aquí agregar validaciones específicas
                // Ejemplo:
                // RequiredString(x => x.CreateDto.Nombre, "El nombre es requerido");
                // ValidEmail(x => x.CreateDto.Email);
            });
        }
    }
}
```

**Validaciones Comunes:**
- `RequiredInformation()` - Campo requerido
- `RequiredString()` - String requerido
- `ValidEmail()` - Email válido
- `ValidRange()` - Rango de valores
- `MaxLength()` - Longitud máxima

---

### 4.2 UPDATE - Actualizar Registro

#### UpdateAreaCommand.cs

```csharp
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class UpdateAreaCommand : CommandBase<GetAreaDto>
    {
        public UpdateAreaCommand(UpdateAreaDto updateDto)
            => UpdateDto = updateDto;

        public UpdateAreaDto UpdateDto { get; set; }
    }
}
```

#### UpdateAreaCommandHandler.cs

```csharp
using AutoMapper;
using MediatR;
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Dto.Base;
using Reclutamiento.Repository.Abstractions.Base;
using Reclutamiento.Repository.Abstractions.Transactions;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class UpdateAreaCommandHandler : CommandHandlerBase<UpdateAreaCommand, GetAreaDto>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public UpdateAreaCommandHandler(
            IUnitOfWork unitOfWork,
            IMapper mapper,
            IMediator mediator,
            UpdateAreaCommandValidator validator,
            IRepository<Entity.Area> AreaRepository
        ) : base(unitOfWork, mapper, mediator, validator)
        {
            _AreaRepository = AreaRepository;
        }

        public override async Task<ResponseDto<GetAreaDto>> HandleCommand(
            UpdateAreaCommand request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<GetAreaDto>();

            // 1. Mapear DTO a Entity
            var Area = _mapper?.Map<Entity.Area>(request.UpdateDto);

            if (Area != null)
            {
                // 2. Actualizar en repositorio
                _AreaRepository.Update(Area);

                // 3. Guardar cambios
                await _AreaRepository.SaveAsync();
            }

            // 4. Mapear a DTO de respuesta
            var AreaDto = _mapper?.Map<GetAreaDto>(Area);
            if (AreaDto != null) response.UpdateData(AreaDto);

            // 5. Mensaje de éxito
            response.AddOkResult(Resources.Common.UpdateSuccessMessage);

            return await Task.FromResult(response);
        }
    }
}
```

#### UpdateAreaCommandValidator.cs

```csharp
using FluentValidation;
using Reclutamiento.Domain.Commands.Base;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class UpdateAreaCommandValidator : CommandValidatorBase<UpdateAreaCommand>
    {
        public UpdateAreaCommandValidator()
        {
            RequiredInformation(x => x.UpdateDto).DependentRules(() =>
            {
                // Validar que el Id existe
                RuleFor(x => x.UpdateDto.IdArea)
                    .GreaterThan(0)
                    .WithMessage("El Id del Area es requerido");

                // Otras validaciones específicas
            });
        }
    }
}
```

---

### 4.3 DELETE - Eliminar Registro (Soft Delete)

#### DeleteAreaCommand.cs

```csharp
using Reclutamiento.Domain.Commands.Base;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class DeleteAreaCommand : CommandBase
    {
        public DeleteAreaCommand(int id) => Id = id;
        public int Id { get; set; }
    }
}
```

**Nota**: DeleteCommand hereda de `CommandBase` (sin genérico) porque no retorna datos.

#### DeleteAreaCommandHandler.cs

```csharp
using AutoMapper;
using MediatR;
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Dto.Base;
using Reclutamiento.Repository.Abstractions.Base;
using Reclutamiento.Repository.Abstractions.Transactions;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class DeleteAreaCommandHandler : CommandHandlerBase<DeleteAreaCommand>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public DeleteAreaCommandHandler(
            IUnitOfWork unitOfWork,
            IMapper mapper,
            IMediator mediator,
            DeleteAreaCommandValidator validator,
            IRepository<Entity.Area> AreaRepository
        ) : base(unitOfWork, mapper, mediator, validator)
        {
            _AreaRepository = AreaRepository;
        }

        public override async Task<ResponseDto> HandleCommand(
            DeleteAreaCommand request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto();

            // 1. Buscar la entidad
            var Area = await _AreaRepository.GetByAsync(x => x.IdArea == request.Id);

            if (Area != null)
            {
                // 2. Soft Delete (marcar como inactivo)
                Area.Activo = false;
                _AreaRepository.Update(Area);

                // 3. Guardar cambios
                await _AreaRepository.SaveAsync();

                // 4. Mensaje de éxito
                response.AddOkResult(Resources.Common.DeleteSuccessMessage);
            }
            else
            {
                // Entidad no encontrada
                response.AddErrorResult("El Area no existe");
            }

            return await Task.FromResult(response);
        }
    }
}
```

**Importante**: El delete es SOFT DELETE (marca `Activo = false`), NO elimina físicamente de la BD.

#### DeleteAreaCommandValidator.cs

```csharp
using FluentValidation;
using Reclutamiento.Domain.Commands.Base;
using Reclutamiento.Repository.Abstractions.Base;

namespace Reclutamiento.Domain.Commands.Dbo.Area
{
    public class DeleteAreaCommandValidator : CommandValidatorBase<DeleteAreaCommand>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public DeleteAreaCommandValidator(
            IRepository<Entity.Area> AreaRepository)
        {
            _AreaRepository = AreaRepository;

            RuleFor(x => x.Id)
                .GreaterThan(0)
                .WithMessage("El Id es requerido");

            // Validar que existe
            RuleFor(x => x.Id)
                .MustAsync(async (id, cancellation) =>
                {
                    var exists = await _AreaRepository
                        .AnyAsync(x => x.IdArea == id && x.Activo);
                    return exists;
                })
                .WithMessage("El Area no existe o ya fue eliminado");

            // Validar que no tiene referencias (opcional)
            // RuleFor(x => x.Id).MustAsync(async (id, cancellation) => {
            //     var hasReferences = await _SubAreaRepository
            //         .AnyAsync(x => x.IdArea == id);
            //     return !hasReferences;
            // }).WithMessage("No se puede eliminar porque tiene SubAreas asociadas");
        }
    }
}
```

---

## Capa 5: Domain - Queries (Lectura CQRS)

### Ubicación
```
Reclutamiento.Domain/Queries/Dbo/Area/
```

### Operaciones de Lectura (5 Queries)

1. **GetAreaQuery** - Obtener un registro por Id
2. **ListAreaQuery** - Listar todos (sin paginación)
3. **SearchAreaQuery** - Búsqueda con filtros y paginación
4. **SelectAreaQuery** - Select con filtros y paginación
5. **SelectComboAreaQuery** - Select simple sin paginación

---

### 5.1 GET - Obtener por Id

#### GetAreaQuery.cs

```csharp
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class GetAreaQuery : QueryBase<GetAreaDto>
    {
        public GetAreaQuery(int id) => Id = id;
        public int Id { get; set; }
    }
}
```

#### GetAreaQueryHandler.cs

```csharp
using AutoMapper;
using Reclutamiento.Dto.Base;
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Repository.Abstractions.Base;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class GetAreaQueryHandler : QueryHandlerBase<GetAreaQuery, GetAreaDto>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public GetAreaQueryHandler(
            IMapper mapper,
            GetAreaQueryValidator validator,
            IRepository<Entity.Area> AreaRepository
        ) : base(mapper, validator)
        {
            _AreaRepository = AreaRepository;
        }

        protected override async Task<ResponseDto<GetAreaDto>> HandleQuery(
            GetAreaQuery request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<GetAreaDto>();

            // 1. Buscar en repositorio
            var Area = await _AreaRepository.GetByAsync(x => x.IdArea == request.Id);

            // 2. Mapear a DTO
            var AreaDto = _mapper?.Map<GetAreaDto>(Area);

            if (Area != null && AreaDto != null)
            {
                response.UpdateData(AreaDto);
            }

            return await Task.FromResult(response);
        }
    }
}
```

#### GetAreaQueryValidator.cs

```csharp
using FluentValidation;
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Repository.Abstractions.Base;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class GetAreaQueryValidator : QueryValidatorBase<GetAreaQuery>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public GetAreaQueryValidator(
            IRepository<Entity.Area> AreaRepository)
        {
            _AreaRepository = AreaRepository;

            RuleFor(x => x.Id)
                .GreaterThan(0)
                .WithMessage("El Id debe ser mayor a 0");

            RuleFor(x => x.Id)
                .MustAsync(async (id, cancellation) =>
                {
                    var exists = await _AreaRepository
                        .AnyAsync(x => x.IdArea == id);
                    return exists;
                })
                .WithMessage("El Area no existe");
        }
    }
}
```

---

### 5.2 LIST - Listar Todos

#### ListAreaQuery.cs

```csharp
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class ListAreaQuery : QueryBase<IEnumerable<ListAreaDto>>
    {
        public ListAreaQuery(int id) => Id = id;
        public int Id { get; set; }
    }
}
```

#### ListAreaQueryHandler.cs

```csharp
using AutoMapper;
using Reclutamiento.Dto.Base;
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Repository.Abstractions.Base;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class ListAreaQueryHandler : QueryHandlerBase<ListAreaQuery, IEnumerable<ListAreaDto>>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public ListAreaQueryHandler(
            IMapper mapper,
            IRepository<Entity.Area> AreaRepository
        ) : base(mapper)
        {
            _AreaRepository = AreaRepository;
        }

        protected override async Task<ResponseDto<IEnumerable<ListAreaDto>>> HandleQuery(
            ListAreaQuery request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<IEnumerable<ListAreaDto>>();

            // 1. Obtener todos los activos
            var Areas = await _AreaRepository
                .GetAllByAsync(x => x.Activo == true);

            // 2. Mapear a DTOs
            var AreaDtos = _mapper?.Map<IEnumerable<ListAreaDto>>(Areas);

            if (AreaDtos != null)
            {
                response.UpdateData(AreaDtos);
            }

            return await Task.FromResult(response);
        }
    }
}
```

---

### 5.3 SEARCH - Búsqueda con Filtros y Paginación

#### SearchAreaQuery.cs

```csharp
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class SearchAreaQuery : SearchQueryBase<SearchAreaFilterDto, SearchAreaDto>
    {
        public SearchAreaQuery(SearchParamsDto<SearchAreaFilterDto> searchParams)
            => SearchParams = searchParams;
    }
}
```

#### SearchAreaQueryHandler.cs

```csharp
using AutoMapper;
using Reclutamiento.Dto.Base;
using Reclutamiento.Entity.Base;
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Repository.Abstractions.Base;
using Reclutamiento.Repository.Extensions;
using System.Linq.Expressions;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class SearchAreaQueryHandler : SearchQueryHandlerBase<SearchAreaQuery, SearchAreaFilterDto, SearchAreaDto>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public SearchAreaQueryHandler(
            IMapper mapper,
            IRepository<Entity.Area> AreaRepository
        ) : base(mapper)
        {
            _AreaRepository = AreaRepository;
        }

        protected override async Task<ResponseDto<SearchResultDto<SearchAreaDto>>> HandleQuery(
            SearchAreaQuery request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<SearchResultDto<SearchAreaDto>>();

            // 1. Construir filtro base
            Expression<Func<Entity.Area, bool>> filter = x => true;

            var filters = request.SearchParams?.Filter;

            // 2. Aplicar filtros específicos
            /*
            if (filters?.FechaDesde.HasValue == true || filters?.FechaHasta.HasValue == true)
            {
                if (filters?.FechaDesde.HasValue == true)
                {
                    var fechaDesde = filters.FechaDesde.GetStartDate();
                    filter = filter.And(x => x.CreateDate >= fechaDesde);
                }

                if (filters?.FechaHasta.HasValue == true)
                {
                    var fechaHasta = filters.FechaHasta.GetEndDate();
                    filter = filter.And(x => x.CreateDate < fechaHasta);
                }
            }
            */

            // Filtro de activos
            filter = filter.And(x => x.Activo == true);

            // 3. Construir ordenamiento
            var sorts = new List<SortExpression<Entity.Area>>();

            if (request.SearchParams?.Sort != null)
            {
                foreach (var srt in request.SearchParams.Sort)
                {
                    var property = IQueryableExtensions
                        .GetSortExpression<Entity.Area>(srt.Direction, srt.Property);
                    if (property != null) sorts.Add(property);
                }
            }

            // 4. Ejecutar búsqueda con paginación
            var Areas = await _AreaRepository.SearchByAsNoTrackingAsync(
                request.SearchParams?.Page?.Page ?? 1,
                request.SearchParams?.Page?.PageSize ?? 10,
                sorts,
                filter
            );

            // 5. Mapear resultados
            var AreaDtos = _mapper?.Map<IEnumerable<SearchAreaDto>>(Areas.Items);

            // 6. Crear resultado paginado
            var searchResult = new SearchResultDto<SearchAreaDto>(
                AreaDtos ?? new List<SearchAreaDto>(),
                Areas.Total,
                request.SearchParams
            );

            response.UpdateData(searchResult);

            return await Task.FromResult(response);
        }
    }
}
```

**Características Clave:**
- Usa `Expression<Func<Entity.Area, bool>>` para filtros dinámicos
- `.And()` extension method para combinar filtros
- `SearchByAsNoTrackingAsync()` para búsquedas read-only eficientes
- Soporta paginación (Page, PageSize)
- Soporta ordenamiento múltiple (Sort)
- Retorna `SearchResultDto<T>` con Items y Total

---

### 5.4 SELECT - Select con Paginación

#### SelectAreaQuery.cs

```csharp
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class SelectAreaQuery : SearchQueryBase<SelectAreaFilterDto, SelectAreaDto>
    {
        public SelectAreaQuery(SearchParamsDto<SelectAreaFilterDto> searchParams)
            => SearchParams = searchParams;
    }
}
```

#### SelectAreaQueryHandler.cs

```csharp
// Implementación similar a SearchAreaQueryHandler
// Usa SelectAreaDto y SelectAreaFilterDto
// Mismo patrón de filtros, ordenamiento y paginación
```

---

### 5.5 SELECT COMBO - Select Simple Sin Paginación

#### SelectComboAreaQuery.cs

```csharp
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class SelectComboAreaQuery : QueryBase<IEnumerable<SelectComboAreaDto>>
    {
        // No recibe parámetros
    }
}
```

#### SelectComboAreaQueryHandler.cs

```csharp
using AutoMapper;
using Reclutamiento.Dto.Base;
using Reclutamiento.Domain.Queries.Base;
using Reclutamiento.Dto.Dbo.Area;
using Reclutamiento.Repository.Abstractions.Base;

namespace Reclutamiento.Domain.Queries.Dbo.Area
{
    public class SelectComboAreaQueryHandler : QueryHandlerBase<SelectComboAreaQuery, IEnumerable<SelectComboAreaDto>>
    {
        private readonly IRepository<Entity.Area> _AreaRepository;

        public SelectComboAreaQueryHandler(
            IMapper mapper,
            IRepository<Entity.Area> AreaRepository
        ) : base(mapper)
        {
            _AreaRepository = AreaRepository;
        }

        protected override async Task<ResponseDto<IEnumerable<SelectComboAreaDto>>> HandleQuery(
            SelectComboAreaQuery request,
            CancellationToken cancellationToken)
        {
            var response = new ResponseDto<IEnumerable<SelectComboAreaDto>>();

            // Obtener solo activos, ordenados por Nombre
            var Areas = await _AreaRepository
                .GetAllByAsNoTrackingAsync(x => x.Activo == true);

            var AreaDtos = _mapper?.Map<IEnumerable<SelectComboAreaDto>>(Areas);

            if (AreaDtos != null)
            {
                response.UpdateData(AreaDtos);
            }

            return await Task.FromResult(response);
        }
    }
}
```

---

## Capa 6: Domain - Resources (Mensajes)

### Ubicación
```
Reclutamiento.Domain/Resources/Dbo/Area.resx
Reclutamiento.Domain/Resources/Dbo/Area.Designer.cs
```

### Responsabilidad
- Almacenar mensajes de validación y errores específicos de la entidad
- Soportar internacionalización (i18n)

### Area.resx (Archivo XML)

```xml
<?xml version="1.0" encoding="utf-8"?>
<root>
  <data name="AreaNotFound" xml:space="preserve">
    <value>El área no fue encontrada</value>
  </data>
  <data name="AreaAlreadyExists" xml:space="preserve">
    <value>Ya existe un área con ese nombre</value>
  </data>
  <data name="AreaHasReferences" xml:space="preserve">
    <value>No se puede eliminar el área porque tiene referencias asociadas</value>
  </data>
</root>
```

### Uso en Validators

```csharp
public class CreateAreaCommandValidator : CommandValidatorBase<CreateAreaCommand>
{
    public CreateAreaCommandValidator()
    {
        RuleFor(x => x.CreateDto.Nombre)
            .NotEmpty()
            .WithMessage(Resources.Dbo.Area.AreaNameRequired);
    }
}
```

---

## Capa 7: Application Abstractions (Interfaces)

### Ubicación
```
Reclutamiento.Application.Abstractions/Dbo/IAreaApplication.cs
```

### Responsabilidad
- Definir contrato de la capa de aplicación
- Una interface por entidad

### Código Real - IAreaApplication.cs

```csharp
using Reclutamiento.Dto.Base;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Application.Abstractions.Dbo
{
    public interface IAreaApplication
    {
        // Commands (Escritura)
        Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto);
        Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto);
        Task<ResponseDto> Delete(int id);

        // Queries (Lectura)
        Task<ResponseDto<GetAreaDto>> Get(int id);
        Task<ResponseDto<IEnumerable<ListAreaDto>>> List(int id);
        Task<ResponseDto<SearchResultDto<SearchAreaDto>>> Search(SearchParamsDto<SearchAreaFilterDto> searchParams);
        Task<ResponseDto<IEnumerable<SelectComboAreaDto>>> SelectCombo();
        Task<ResponseDto<SearchResultDto<SelectAreaDto>>> Select(SearchParamsDto<SelectAreaFilterDto> searchParams);
    }
}
```

### Patrón Estándar

**SIEMPRE incluir estos 8 métodos:**

1. `Create()` - Crear
2. `Update()` - Actualizar
3. `Delete()` - Eliminar
4. `Get()` - Obtener por Id
5. `List()` - Listar todos
6. `Search()` - Búsqueda paginada
7. `Select()` - Select paginado
8. `SelectCombo()` - Combo simple

---

## Capa 8: Application (Implementación)

### Ubicación
```
Reclutamiento.Application/Dbo/AreaApplication.cs
```

### Responsabilidad
- Implementar la interface IAreaApplication
- Delegar a MediatR para enviar Commands/Queries

### Código Real - AreaApplication.cs

```csharp
using MediatR;
using Reclutamiento.Dto.Base;
using Reclutamiento.Application.Abstractions.Dbo;
using Reclutamiento.Application.Base;
using Reclutamiento.Domain.Commands.Dbo.Area;
using Reclutamiento.Domain.Queries.Dbo.Area;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Application.Dbo
{
    public class AreaApplication : ApplicationBase, IAreaApplication
    {
        public AreaApplication(IMediator mediator) : base(mediator)
        {
        }

        // COMMANDS
        public async Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto)
            => await _mediator.Send(new CreateAreaCommand(createDto));

        public async Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto)
            => await _mediator.Send(new UpdateAreaCommand(updateDto));

        public async Task<ResponseDto> Delete(int id)
            => await _mediator.Send(new DeleteAreaCommand(id));

        // QUERIES
        public async Task<ResponseDto<GetAreaDto>> Get(int id)
            => await _mediator.Send(new GetAreaQuery(id));

        public async Task<ResponseDto<IEnumerable<ListAreaDto>>> List(int id)
            => await _mediator.Send(new ListAreaQuery(id));

        public async Task<ResponseDto<SearchResultDto<SearchAreaDto>>> Search(
            SearchParamsDto<SearchAreaFilterDto> searchParams)
            => await _mediator.Send(new SearchAreaQuery(searchParams));

        public async Task<ResponseDto<IEnumerable<SelectComboAreaDto>>> SelectCombo()
            => await _mediator.Send(new SelectComboAreaQuery());

        public async Task<ResponseDto<SearchResultDto<SelectAreaDto>>> Select(
            SearchParamsDto<SelectAreaFilterDto> searchParams)
             => await _mediator.Send(new SelectAreaQuery(searchParams));
    }
}
```

### Patrón

**Cada método:**
1. Crea el Command/Query correspondiente
2. Lo envía usando `_mediator.Send()`
3. Retorna el resultado directamente

**NO hay lógica de negocio aquí**, solo orquestación con MediatR.

---

## Capa 9: API - Controllers

### Ubicación
```
Reclutamiento.Apis/Controllers/Dbo/AreaController.cs
```

### Responsabilidad
- Exponer endpoints HTTP
- Delegar a la capa de Application
- Implementar la misma interface que Application (IAreaApplication)

### Código Real - AreaController.cs

```csharp
using Microsoft.AspNetCore.Mvc;
using Reclutamiento.Dto.Base;
using Reclutamiento.Application.Abstractions.Dbo;
using Reclutamiento.Dto.Dbo.Area;

namespace Reclutamiento.Apis.Controllers.Dbo
{
    [ApiController]
    [Route("api/Area")]
    public class AreaController : IAreaApplication
    {
        private readonly IAreaApplication _AreaApplication;

        public AreaController(IAreaApplication AreaApplication)
            => _AreaApplication = AreaApplication;

        // POST /api/Area
        [HttpPost]
        public async Task<ResponseDto<GetAreaDto>> Create(CreateAreaDto createDto)
            => await _AreaApplication.Create(createDto);

        // PUT /api/Area
        [HttpPut]
        public async Task<ResponseDto<GetAreaDto>> Update(UpdateAreaDto updateDto)
            => await _AreaApplication.Update(updateDto);

        // DELETE /api/Area/{id}
        [HttpDelete("{id}")]
        public async Task<ResponseDto> Delete(int id)
            => await _AreaApplication.Delete(id);

        // GET /api/Area/{id}
        [HttpGet("{id}")]
        public async Task<ResponseDto<GetAreaDto>> Get(int id)
            => await _AreaApplication.Get(id);

        // POST /api/Area/list
        [HttpPost("list")]
        public async Task<ResponseDto<IEnumerable<ListAreaDto>>> List(int id)
            => await _AreaApplication.List(id);

        // POST /api/Area/search
        [HttpPost("search")]
        public async Task<ResponseDto<SearchResultDto<SearchAreaDto>>> Search(
            SearchParamsDto<SearchAreaFilterDto> searchParams)
            => await _AreaApplication.Search(searchParams);

        // GET /api/Area/selectcombo
        [HttpGet("selectcombo")]
        public async Task<ResponseDto<IEnumerable<SelectComboAreaDto>>> SelectCombo()
            => await _AreaApplication.SelectCombo();

        // POST /api/Area/select
        [HttpPost("select")]
        public async Task<ResponseDto<SearchResultDto<SelectAreaDto>>> Select(
            SearchParamsDto<SelectAreaFilterDto> searchParams)
            => await _AreaApplication.Select(searchParams);
    }
}
```

### Endpoints Estándar

| Método | Ruta | Acción | Body |
|--------|------|--------|------|
| POST | `/api/Area` | Create | CreateAreaDto |
| PUT | `/api/Area` | Update | UpdateAreaDto |
| DELETE | `/api/Area/{id}` | Delete | - |
| GET | `/api/Area/{id}` | Get | - |
| POST | `/api/Area/list` | List | int id |
| POST | `/api/Area/search` | Search | SearchParamsDto |
| GET | `/api/Area/selectcombo` | SelectCombo | - |
| POST | `/api/Area/select` | Select | SearchParamsDto |

---

## Flujo Completo de una Operación

### Ejemplo: Crear un Área Nueva

```
1. HTTP Request
   ↓
   POST /api/Area
   Body: { "nombre": "Recursos Humanos" }

2. AreaController.Create(CreateAreaDto)
   ↓
   Inyecta: IAreaApplication

3. AreaApplication.Create(CreateAreaDto)
   ↓
   _mediator.Send(new CreateAreaCommand(createDto))

4. MediatR enruta a CreateAreaCommandHandler
   ↓
   ANTES: CreateAreaCommandValidator valida el comando

5. CreateAreaCommandHandler.HandleCommand()
   ↓
   a) UnitOfWork.BeginTransaction()
   b) Mapear CreateAreaDto → Entity.Area (AutoMapper usa AreaProfile)
   c) _AreaRepository.AddAsync(Area)
   d) _AreaRepository.SaveAsync()
   e) Mapear Entity.Area → GetAreaDto
   f) UnitOfWork.Commit()
   g) Auditoría automática (UserNameCreate, CreateDate)

6. Retorna ResponseDto<GetAreaDto>
   ↓
   {
     "isValid": true,
     "messages": [{"message": "Creado exitosamente"}],
     "data": {
       "idArea": 123,
       "nombre": "Recursos Humanos",
       "activo": true
     }
   }

7. HTTP Response
   ↓
   200 OK con JSON
```

### Capas Atravesadas

```
Controller (Presentación)
    ↓
Application (Orquestación)
    ↓
MediatR (Mediator)
    ↓
CommandHandler (Lógica de Negocio)
    ↓
Repository (Acceso a Datos)
    ↓
Entity Framework Core
    ↓
SQL Server Database
```

---

## Checklist de Implementación

### Para Agregar una Nueva Entidad (Ej: "Proyecto")

#### ✅ Paso 1: Entity Layer
- [ ] Crear `Proyecto.cs` en `Reclutamiento.Entity/`
- [ ] Incluir propiedades de auditoría estándar
- [ ] Configurar Primary Key: `IdProyecto`

#### ✅ Paso 2: DTOs Layer
- [ ] Crear carpeta `Reclutamiento.Dto/Dbo/Proyecto/`
- [ ] Crear `ProyectoDto.cs` (base con propiedades de negocio)
- [ ] Crear `CreateProyectoDto.cs` (hereda de ProyectoDto)
- [ ] Crear `UpdateProyectoDto.cs` (hereda de ProyectoDto + IdProyecto)
- [ ] Crear `GetProyectoDto.cs` (hereda de ProyectoDto + IdProyecto + Activo)
- [ ] Crear `ListProyectoDto.cs` (hereda de ProyectoDto)
- [ ] Crear `SearchProyectoDto.cs` (hereda de ProyectoDto + campos para grilla)
- [ ] Crear `SearchProyectoFilterDto.cs` (filtros con todos nullable)
- [ ] Crear `SelectProyectoDto.cs` (hereda de ProyectoDto)
- [ ] Crear `SelectProyectoFilterDto.cs` (filtros)
- [ ] Crear `SelectComboProyectoDto.cs` (hereda de ProyectoDto)

#### ✅ Paso 3: Mapping Layer
- [ ] Crear `Reclutamiento.Domain/Mapping/Dbo/ProyectoProfile.cs`
- [ ] Configurar mapeos bidireccionales para todos los DTOs
- [ ] Usar `.ReverseMap()` para mapeo inverso automático

#### ✅ Paso 4: Commands Layer
- [ ] Crear carpeta `Reclutamiento.Domain/Commands/Dbo/Proyecto/`
- [ ] **CREATE**: Command + Handler + Validator (3 archivos)
- [ ] **UPDATE**: Command + Handler + Validator (3 archivos)
- [ ] **DELETE**: Command + Handler + Validator (3 archivos)

#### ✅ Paso 5: Queries Layer
- [ ] Crear carpeta `Reclutamiento.Domain/Queries/Dbo/Proyecto/`
- [ ] **GET**: Query + Handler + Validator (3 archivos)
- [ ] **LIST**: Query + Handler (2 archivos)
- [ ] **SEARCH**: Query + Handler (2 archivos)
- [ ] **SELECT**: Query + Handler (2 archivos)
- [ ] **SELECT COMBO**: Query + Handler (2 archivos)

#### ✅ Paso 6: Resources Layer
- [ ] Crear `Reclutamiento.Domain/Resources/Dbo/Proyecto.resx`
- [ ] Agregar mensajes de validación y errores
- [ ] Visual Studio genera automáticamente `Proyecto.Designer.cs`

#### ✅ Paso 7: Application Abstractions Layer
- [ ] Crear `Reclutamiento.Application.Abstractions/Dbo/IProyectoApplication.cs`
- [ ] Definir los 8 métodos estándar (Create, Update, Delete, Get, List, Search, Select, SelectCombo)

#### ✅ Paso 8: Application Layer
- [ ] Crear `Reclutamiento.Application/Dbo/ProyectoApplication.cs`
- [ ] Implementar `IProyectoApplication`
- [ ] Delegar cada método a MediatR

#### ✅ Paso 9: API Layer
- [ ] Crear `Reclutamiento.Apis/Controllers/Dbo/ProyectoController.cs`
- [ ] Implementar `IProyectoApplication`
- [ ] Decorar métodos con atributos HTTP ([HttpPost], [HttpGet], etc.)
- [ ] Configurar rutas con [Route]

#### ✅ Paso 10: Dependency Injection
- [ ] Los repositories se registran automáticamente (genéricos)
- [ ] Los handlers se registran automáticamente (MediatR escanea)
- [ ] Los validators se registran automáticamente (FluentValidation escanea)
- [ ] Registrar `IProyectoApplication` → `ProyectoApplication` en DI container

#### ✅ Paso 11: Testing
- [ ] Probar endpoint POST /api/Proyecto (Create)
- [ ] Probar endpoint PUT /api/Proyecto (Update)
- [ ] Probar endpoint DELETE /api/Proyecto/{id} (Delete)
- [ ] Probar endpoint GET /api/Proyecto/{id} (Get)
- [ ] Probar endpoint POST /api/Proyecto/search (Search)
- [ ] Probar endpoint GET /api/Proyecto/selectcombo (SelectCombo)

---

## Convenciones de Nomenclatura

### Archivos

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Entity | `{Entidad}.cs` | `Area.cs` |
| DTO Base | `{Entidad}Dto.cs` | `AreaDto.cs` |
| DTO Create | `Create{Entidad}Dto.cs` | `CreateAreaDto.cs` |
| DTO Update | `Update{Entidad}Dto.cs` | `UpdateAreaDto.cs` |
| DTO Get | `Get{Entidad}Dto.cs` | `GetAreaDto.cs` |
| DTO List | `List{Entidad}Dto.cs` | `ListAreaDto.cs` |
| DTO Search | `Search{Entidad}Dto.cs` | `SearchAreaDto.cs` |
| DTO Filter | `Search{Entidad}FilterDto.cs` | `SearchAreaFilterDto.cs` |
| Command | `{Accion}{Entidad}Command.cs` | `CreateAreaCommand.cs` |
| Handler | `{Accion}{Entidad}CommandHandler.cs` | `CreateAreaCommandHandler.cs` |
| Validator | `{Accion}{Entidad}CommandValidator.cs` | `CreateAreaCommandValidator.cs` |
| Query | `{Accion}{Entidad}Query.cs` | `GetAreaQuery.cs` |
| Query Handler | `{Accion}{Entidad}QueryHandler.cs` | `GetAreaQueryHandler.cs` |
| Mapper Profile | `{Entidad}Profile.cs` | `AreaProfile.cs` |
| Interface | `I{Entidad}Application.cs` | `IAreaApplication.cs` |
| Application | `{Entidad}Application.cs` | `AreaApplication.cs` |
| Controller | `{Entidad}Controller.cs` | `AreaController.cs` |

### Propiedades

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Primary Key | `Id{Entidad}` | `IdArea` |
| Foreign Key | `Id{EntidadRelacionada}` | `IdGerencia` |
| Nombre | `Nombre` | `Nombre` |
| Descripción | `Descripcion` | `Descripcion` |
| Código | `Codigo` | `Codigo` |
| Fecha | `Fecha{Concepto}` | `FechaInicio` |
| Flag booleano | `{Concepto}` | `Activo`, `Aprobado` |

### Namespaces

```csharp
// Entity
namespace Reclutamiento.Entity;

// DTOs
namespace Reclutamiento.Dto.Dbo.Area;

// Commands
namespace Reclutamiento.Domain.Commands.Dbo.Area;

// Queries
namespace Reclutamiento.Domain.Queries.Dbo.Area;

// Mapping
namespace Reclutamiento.Domain.Mapping.Area;

// Application Abstractions
namespace Reclutamiento.Application.Abstractions.Dbo;

// Application
namespace Reclutamiento.Application.Dbo;

// Controllers
namespace Reclutamiento.Apis.Controllers.Dbo;
```

---

## Resumen Visual de Archivos por Capa

```
📁 Reclutamiento.Entity/
   └── 📄 Area.cs                                          [1 archivo]

📁 Reclutamiento.Dto/Dbo/Area/
   ├── 📄 AreaDto.cs
   ├── 📄 CreateAreaDto.cs
   ├── 📄 UpdateAreaDto.cs
   ├── 📄 GetAreaDto.cs
   ├── 📄 ListAreaDto.cs
   ├── 📄 SearchAreaDto.cs
   ├── 📄 SearchAreaFilterDto.cs
   ├── 📄 SelectAreaDto.cs
   ├── 📄 SelectAreaFilterDto.cs
   ├── 📄 SelectComboAreaDto.cs
   └── 📄 AreaFilterDto.cs                                [11 archivos]

📁 Reclutamiento.Domain/Mapping/Dbo/
   └── 📄 AreaProfile.cs                                   [1 archivo]

📁 Reclutamiento.Domain/Commands/Dbo/Area/
   ├── 📄 CreateAreaCommand.cs
   ├── 📄 CreateAreaCommandHandler.cs
   ├── 📄 CreateAreaCommandValidator.cs
   ├── 📄 UpdateAreaCommand.cs
   ├── 📄 UpdateAreaCommandHandler.cs
   ├── 📄 UpdateAreaCommandValidator.cs
   ├── 📄 DeleteAreaCommand.cs
   ├── 📄 DeleteAreaCommandHandler.cs
   └── 📄 DeleteAreaCommandValidator.cs                    [9 archivos]

📁 Reclutamiento.Domain/Queries/Dbo/Area/
   ├── 📄 GetAreaQuery.cs
   ├── 📄 GetAreaQueryHandler.cs
   ├── 📄 GetAreaQueryValidator.cs
   ├── 📄 ListAreaQuery.cs
   ├── 📄 ListAreaQueryHandler.cs
   ├── 📄 SearchAreaQuery.cs
   ├── 📄 SearchAreaQueryHandler.cs
   ├── 📄 SelectAreaQuery.cs
   ├── 📄 SelectAreaQueryHandler.cs
   ├── 📄 SelectComboAreaQuery.cs
   └── 📄 SelectComboAreaQueryHandler.cs                   [11 archivos]

📁 Reclutamiento.Domain/Resources/Dbo/
   ├── 📄 Area.resx
   └── 📄 Area.Designer.cs                                 [2 archivos]

📁 Reclutamiento.Application.Abstractions/Dbo/
   └── 📄 IAreaApplication.cs                              [1 archivo]

📁 Reclutamiento.Application/Dbo/
   └── 📄 AreaApplication.cs                               [1 archivo]

📁 Reclutamiento.Apis/Controllers/Dbo/
   └── 📄 AreaController.cs                                [1 archivo]

──────────────────────────────────────────────────────────
TOTAL: 38 archivos por entidad
```

---

## Conclusión

Este documento presenta el patrón completo de implementación de una entidad en el proyecto Reclutamiento (.NET).

**Puntos Clave:**

1. **Consistencia Total**: Las 25 entidades siguen exactamente el mismo patrón
2. **Separación por Capas**: Cada capa tiene una responsabilidad clara
3. **Carpetas Dbo**: Organización consistente en todas las capas
4. **CQRS Puro**: Commands (escritura) y Queries (lectura) separados
5. **Validación Declarativa**: FluentValidation en cada Command/Query
6. **Mapeo Automático**: AutoMapper con Profiles por entidad
7. **Auditoría Automática**: Campos de auditoría en todas las entidades
8. **Soft Delete**: Delete marca como inactivo, no elimina físicamente

**Este patrón asegura:**
- Mantenibilidad
- Escalabilidad
- Testabilidad
- Bajo acoplamiento
- Alta cohesión

---

**Versión:** 1.0
**Fecha:** 2025-01-04
**Basado en:** Proyecto Reclutamiento .NET - Entidad Area
