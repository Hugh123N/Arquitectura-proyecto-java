# Implementación de Capa Domain - Reclutamiento

## Resumen

Se ha implementado exitosamente la capa Application siguiendo el patrón CQRS del proyecto .NET original, adaptado a Spring Boot 3 y Java 21 con PipelinR.

**Estado**: ✅ Implementación completa con arquitectura base y validación automática

---

## Estructura Implementada

### 📦 Módulo: reclutamiento-entity

---

## 1. Commands - Patrón CQRS

### 📁 Commands Base (`commands/base/`)

#### **BaseCommand**
- **Ubicación**: `com.soft.reclutamiento.entity.commands.base.BaseCommand`
- **Equivalente .NET**: `CommandBase` (sin tipo de respuesta)
- **Extiende**: `Command<ResponseDto>` (PipelinR)
- **Características**:
  - Flag `validate` para habilitar/deshabilitar validación
  - Métodos: `isValidate()`, `enableValidation()`, `disableValidation()`
  - Se usa para comandos que no retornan datos específicos (ej: Delete)

```java
public abstract class BaseCommand implements Command<ResponseDto> {
    private boolean validate = true;

    public boolean isValidate() { return validate; }
    public void enableValidation() { this.validate = true; }
    public void disableValidation() { this.validate = false; }
}
```

#### **CommandWithResponse<TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.commands.base.CommandWithResponse`
- **Equivalente .NET**: `CommandBase<TResponse>`
- **Extiende**: `Command<ResponseDto<TResponse>>` (PipelinR)
- **Características**:
  - Similar a BaseCommand pero con tipo de respuesta genérico
  - Se usa para comandos que retornan datos (ej: Create, Update)

```java
public class CommandWithResponse<TResponse> implements Command<ResponseDto<TResponse>> {
    private boolean validate = true;
    // ... métodos de validación
}
```

---

#### **CommandValidatorBase<TCommand>**
- **Ubicación**: `com.soft.reclutamiento.entity.commands.base.CommandValidatorBase`
- **Equivalente .NET**: `CommandValidatorBase<TRequest>` (con FluentValidation)
- **Características**:
  - Flag `enabled` para habilitar/deshabilitar validador
  - Métodos helper para validaciones comunes
  - Integración con Jakarta Bean Validation
  - Acceso a `Messages` para mensajes i18n

**Métodos Helper**:
```java
// Validar campo requerido
protected boolean isRequired(Object value, String fieldName, List<String> errors)

// Validar longitud máxima
protected boolean maxLength(String value, int maxLength, String fieldName, List<String> errors)

// Validar longitud mínima
protected boolean minLength(String value, int minLength, String fieldName, List<String> errors)

// Validar email
protected boolean isValidEmail(String email, String fieldName, List<String> errors)

// Crear lista de errores
protected List<String> createErrorList()
```

**Comparación .NET → Java**:
```csharp
// .NET - FluentValidation
public class CreateAreaCommandValidator : CommandValidatorBase<CreateAreaCommand>
{
    public CreateAreaCommandValidator()
    {
        RequiredInformation(x => x.CreateDto).DependentRules(() => {
            RequiredString(x => x.CreateDto.Nombre, "Nombre", maximumLength: 255);
        });
    }
}
```

```java
// Java - CommandValidatorBase + Jakarta Validation
@Component
public class CreateAreaCommandValidator extends CommandValidatorBase<CreateAreaCommand> {
    private final Validator validator;

    @Override
    public List<String> validate(CreateAreaCommand command) {
        var errors = createErrorList();

        if (command.getCreateDto() == null) {
            errors.add(messages.informationRequired());
            return errors;
        }

        // Jakarta Bean Validation
        Set<ConstraintViolation<Object>> violations = validator.validate(command.getCreateDto());
        for (ConstraintViolation<Object> violation : violations) {
            errors.add(violation.getMessage());
        }

        return errors;
    }
}
```

---

#### **CommandHandlerBase<TCommand>**
- **Ubicación**: `com.soft.reclutamiento.entity.commands.base.CommandHandlerBase`
- **Equivalente .NET**: `CommandHandlerBase<TRequest>` (sin respuesta tipada)
- **Implementa**: `Command.Handler<TCommand, ResponseDto>` (PipelinR)
- **Características**:
  - ✅ **Validación automática** antes de ejecutar el comando
  - ✅ **Manejo de transacciones** con `@Transactional`
  - ✅ **Manejo de excepciones** centralizado
  - Acceso a `Messages` para respuestas i18n
  - Método abstracto `handleCommand()` para lógica de negocio

**Flujo de Ejecución**:
```
1. handle(command) llamado por PipelinR
   ↓
2. Verifica si validación está habilitada
   ↓
3. Si hay validator: llama validator.validate()
   ↓
4. Si hay errores: retorna ResponseDto con errores
   ↓
5. Si no hay errores: llama handleCommand() (lógica de negocio)
   ↓
6. Maneja excepciones automáticamente
```

**Comparación .NET → Java**:
```csharp
// .NET
public class DeleteAreaCommandHandler : CommandHandlerBase<DeleteAreaCommand>
{
    public DeleteAreaCommandHandler(
        IUnitOfWork unitOfWork,
        IMapper mapper,
        DeleteAreaCommandValidator validator
    ) : base(unitOfWork, mapper, null, validator) { }

    public override async Task<ResponseDto> HandleCommand(
        DeleteAreaCommand request,
        CancellationToken cancellationToken)
    {
        var response = new ResponseDto();
        var area = await _repository.GetByIdAsync(request.Id);
        area.Activo = false;
        await _repository.SaveAsync();
        response.AddOkResult(Resources.Common.DeleteSuccessMessage);
        return response;
    }
}
```

```java
// Java - ¡Mucho más simple!
@Component
public class DeleteAreaCommandHandler extends CommandHandlerBase<DeleteAreaCommand> {

    private final AreaRepository areaRepository;

    public DeleteAreaCommandHandler(
            Messages messages,
            DeleteAreaCommandValidator validator,
            AreaRepository areaRepository
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
    }

    @Override
    protected ResponseDto handleCommand(DeleteAreaCommand command) {
        var response = new ResponseDto();

        // Solo lógica de negocio - validación automática
        var area = areaRepository.findById(command.getId()).get();
        area.setActivo(false);
        areaRepository.save(area);

        response.addOkResult(messages.deleteSuccess());
        return response;
    }
}
```

---

#### **CommandHandlerBaseWithResponse<TCommand, TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.commands.base.CommandHandlerBaseWithResponse`
- **Equivalente .NET**: `CommandHandlerBase<TRequest, TResponse>`
- **Implementa**: `Command.Handler<TCommand, ResponseDto<TResponse>>` (PipelinR)
- **Características**:
  - Igual que CommandHandlerBase pero con respuesta tipada
  - Se usa para Create y Update que retornan DTOs

**Ejemplo de Uso**:
```java
@Component
public class CreateAreaCommandHandler
    extends CommandHandlerBaseWithResponse<CreateAreaCommand, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public CreateAreaCommandHandler(
            Messages messages,
            CreateAreaCommandValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
        var response = new ResponseDto<GetAreaDto>();

        // Mapear y guardar
        Area area = areaMapper.toEntity(command.getCreateDto());
        area = areaRepository.save(area);

        // Mapear respuesta
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);
        response.addOkResult(messages.createSuccess());

        return response;
    }
}
```

---

## 2. Queries - Patrón CQRS

### 📁 Queries Base (`queries/base/`)

#### **Query<TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.Query`
- **Equivalente .NET**: `QueryBase<TResponse>`
- **Extiende**: `Command<ResponseDto<TResponse>>` (PipelinR - sí, Query extiende Command)
- **Características**:
  - Clase abstracta base para todas las queries
  - No tiene flag de validación (queries siempre validan si hay validator)

```java
public abstract class Query<TResponse> implements Command<ResponseDto<TResponse>> {
    // Clase base vacía - solo define el contrato
}
```

---

#### **QueryValidatorBase<TQuery>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.QueryValidatorBase`
- **Equivalente .NET**: `QueryValidatorBase<TRequest>`
- **Características**:
  - Similar a CommandValidatorBase pero para queries
  - Métodos helper para validaciones comunes
  - Acceso a `Messages` para mensajes i18n

```java
public abstract class QueryValidatorBase<TQuery> {
    @Autowired
    protected Messages messages;

    public abstract List<String> validate(TQuery query);

    protected boolean isRequired(Object value, String fieldName, List<String> errors);
    protected List<String> createErrorList();
}
```

---

#### **QueryHandlerBase<TQuery, TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.QueryHandlerBase`
- **Equivalente .NET**: `QueryHandlerBase<TRequest, TResponse>`
- **Implementa**: `Command.Handler<TQuery, ResponseDto<TResponse>>` (PipelinR)
- **Características**:
  - ✅ **Validación automática** antes de ejecutar la query
  - ✅ **Manejo de excepciones** centralizado
  - NO usa `@Transactional` (queries son read-only)
  - Método abstracto `handleQuery()` para lógica de consulta

**Flujo de Ejecución**:
```
1. handle(query) llamado por PipelinR
   ↓
2. Si hay validator: llama validator.validate()
   ↓
3. Si hay errores: retorna ResponseDto con errores
   ↓
4. Si no hay errores: llama handleQuery() (lógica de consulta)
   ↓
5. Maneja excepciones automáticamente
```

**Ejemplo de Uso**:
```java
@Component
public class GetAreaQueryHandler extends QueryHandlerBase<GetAreaQuery, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public GetAreaQueryHandler(
            Messages messages,
            GetAreaQueryValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleQuery(GetAreaQuery query) {
        var response = new ResponseDto<GetAreaDto>();

        // Obtener y mapear - validación automática
        var area = areaRepository.findById(query.getId()).get();
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);

        return response;
    }
}
```

---

## 3. Search Queries - Búsqueda Paginada

### 📁 Search Queries Base (`queries/base/`)

#### **SearchQueryBase<TFilter, TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.SearchQueryBase`
- **Equivalente .NET**: `SearchQueryBase<TFilter, TResponse>`
- **Extiende**: `Query<SearchResultDto<TResponse>>`
- **Características**:
  - Encapsula parámetros de búsqueda: filtros, paginación, ordenamiento
  - Propiedad `searchParams` de tipo `SearchParamsDto<TFilter>`

```java
public abstract class SearchQueryBase<TFilter, TResponse>
    extends Query<SearchResultDto<TResponse>> {

    private SearchParamsDto<TFilter> searchParams;

    public SearchQueryBase(SearchParamsDto<TFilter> searchParams) {
        this.searchParams = searchParams;
    }

    // Getters y Setters
}
```

**Comparación .NET → Java**:
```csharp
// .NET
public class SearchAreaQuery : SearchQueryBase<SearchAreaFilterDto, SearchAreaDto>
{
    public SearchAreaQuery(SearchParamsDto<SearchAreaFilterDto> searchParams)
        : base(searchParams) { }
}
```

```java
// Java - Idéntico!
public class SearchAreaQuery
    extends SearchQueryBase<SearchAreaFilterDto, SearchAreaDto> {

    public SearchAreaQuery(SearchParamsDto<SearchAreaFilterDto> searchParams) {
        super(searchParams);
    }
}
```

---

#### **SearchQueryValidatorBase<TQuery, TFilter, TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.SearchQueryValidatorBase`
- **Equivalente .NET**: `SearchQueryValidatorBase<TRequest, TFilter, TResponse>`
- **Extiende**: `QueryValidatorBase<TQuery>`
- **Características**:
  - ✅ **Validación automática** de parámetros de búsqueda
  - Valida paginación (page > 0, pageSize entre 1-1000)
  - Valida ordenamiento (direction debe ser "asc" o "desc")
  - Se usa automáticamente si no se proporciona validator personalizado

**Validaciones Automáticas**:
```java
// Valida SearchParams no null
// Valida Page no null
// Valida Page.page > 0
// Valida Page.pageSize > 0 y <= 1000
// Valida Sort.direction en ["asc", "desc"]
```

**Comparación .NET → Java**:
```csharp
// .NET - FluentValidation
public class SearchQueryValidatorBase<TRequest, TFilter, TResponse>
    : QueryValidatorBase<TRequest>
    where TRequest : SearchQueryBase<TFilter, TResponse>
{
    private readonly string[] SortDirections = ["asc", "desc"];

    public SearchQueryValidatorBase()
    {
        RequiredInformation(x => x.SearchParams, Resources.Common.SearchInformationRequired)
            .DependentRules(() =>
            {
                RequiredInformation(x => x.SearchParams.Page, ...)
                    .DependentRules(() =>
                    {
                        RequiredField(x => x.SearchParams.Page!.Page, ...)
                            .GreaterThan(0)...;
                        RequiredField(x => x.SearchParams.Page!.PageSize, ...)
                            .GreaterThan(0)...
                            .LessThanOrEqualTo(1000)...;
                    });

                RuleFor(x => x.SearchParams.Sort).Must((request, sort, context) =>
                {
                    if (sort == null) return true;
                    var invalidSortDirs = sort.Where(x => !SortDirections.Contains(x.Direction));
                    if (invalidSortDirs.Any())
                        return CustomValidationMessage(context, Resources.Common.SortDirectionNotValid);
                    return true;
                });
            });
    }
}
```

```java
// Java - Validación imperativa (más simple y clara)
@Override
public List<String> validate(TQuery query) {
    var errors = createErrorList();

    // Validar que existan searchParams
    if (query.getSearchParams() == null) {
        errors.add(messages.get("common.search.information.required"));
        return errors;
    }

    var searchParams = query.getSearchParams();

    // Validar paginación
    if (searchParams.getPage() == null) {
        errors.add(messages.get("common.search.page.information.required"));
    } else {
        var page = searchParams.getPage();

        // Validar número de página
        if (page.getPage() == 0) {
            errors.add(messages.fieldRequired(messages.get("common.page.field")));
        } else if (page.getPage() <= 0) {
            errors.add(messages.get("common.page.field.min.value"));
        }

        // Validar tamaño de página
        if (page.getPageSize() == 0) {
            errors.add(messages.fieldRequired(messages.get("common.page.size.field")));
        } else {
            if (page.getPageSize() <= 0) {
                errors.add(messages.get("common.page.size.field.min.value"));
            }
            if (page.getPageSize() > 1000) {
                errors.add(messages.get("common.page.size.field.max.value"));
            }
        }
    }

    // Validar ordenamiento
    if (searchParams.getSort() != null && !searchParams.getSort().isEmpty()) {
        for (var sort : searchParams.getSort()) {
            if (sort.getDirection() == null) {
                errors.add(messages.get("common.sort.direction.required"));
            } else {
                boolean validDirection = false;
                for (String validDir : SORT_DIRECTIONS) {
                    if (validDir.equalsIgnoreCase(sort.getDirection())) {
                        validDirection = true;
                        break;
                    }
                }
                if (!validDirection) {
                    errors.add(messages.get("common.sort.direction.not.valid"));
                }
            }
        }
    }

    return errors;
}
```

---

#### **SearchQueryHandlerBase<TQuery, TFilter, TResponse>**
- **Ubicación**: `com.soft.reclutamiento.entity.queries.base.SearchQueryHandlerBase`
- **Equivalente .NET**: `SearchQueryHandlerBase<TRequest, TFilter, TResponse>`
- **Extiende**: `QueryHandlerBase<TQuery, SearchResultDto<TResponse>>`
- **Características**:
  - ✅ **Validación automática** de parámetros de búsqueda
  - Si no hay validator personalizado, usa `SearchQueryValidatorBase` automáticamente
  - Método abstracto `handleQuery()` para lógica de búsqueda

**Flujo de Ejecución**:
```
1. handle(query) llamado por PipelinR
   ↓
2. Si no hay validator: crea SearchQueryValidatorBase automáticamente
   ↓
3. Llama validator.validate() - valida paginación/ordenamiento
   ↓
4. Si hay errores: retorna ResponseDto con errores
   ↓
5. Si no hay errores: llama handleQuery() (lógica de búsqueda)
   ↓
6. Maneja excepciones automáticamente
```

**Ejemplo de Uso Completo**:
```java
@Component
public class SearchAreaQueryHandler
    extends SearchQueryHandlerBase<SearchAreaQuery, SearchAreaFilterDto, SearchAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    // Constructor simple - validación automática
    public SearchAreaQueryHandler(Messages messages,
                                   AreaRepository areaRepository,
                                   AreaMapper areaMapper) {
        super(messages); // No necesita validator - usa el automático
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<SearchResultDto<SearchAreaDto>> handleQuery(SearchAreaQuery query) {
        var response = new ResponseDto<SearchResultDto<SearchAreaDto>>();

        // Construir filtros con JPA Specification
        Specification<Area> specification = (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();

            // Filtro por activo = true
            predicates.add(criteriaBuilder.equal(root.get("activo"), true));

            // Filtros adicionales
            var filters = query.getSearchParams().getFilter();
            if (filters != null) {
                if (filters.getIdArea() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("idArea"), filters.getIdArea()));
                }
                if (filters.getFechaDesde() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createDate"), filters.getFechaDesde()));
                }
                if (filters.getFechaHasta() != null) {
                    predicates.add(criteriaBuilder.lessThan(
                        root.get("createDate"), filters.getFechaHasta()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        // Construir ordenamiento
        List<SortExpression<Area>> sortExpressions = new ArrayList<>();
        if (query.getSearchParams().getSort() != null) {
            query.getSearchParams().getSort().forEach(sort -> {
                SortDirection direction = SortDirection.valueOf(sort.getDirection().toUpperCase());
                sortExpressions.add(new SortExpression<>(direction, sort.getProperty()));
            });
        }

        // Ejecutar búsqueda
        int page = query.getSearchParams().getPage().getPage();
        int pageSize = query.getSearchParams().getPage().getPageSize();

        SearchResult<Area> searchResult = areaRepository.searchByAsNoTracking(
            page, pageSize, sortExpressions, specification);

        // Mapear a DTOs
        List<SearchAreaDto> areaDtos = searchResult.getItems().stream()
                .map(areaMapper::toSearchAreaDto)
                .collect(Collectors.toList());

        SearchResultDto<SearchAreaDto> resultDto = new SearchResultDto<>(
                areaDtos,
                searchResult.getTotal(),
                query.getSearchParams()
        );

        response.updateData(resultDto);
        return response;
    }
}
```

---

## 4. Resources - Internacionalización (i18n)

### 📁 Resources (`resources/`)

#### **Messages**
- **Ubicación**: `com.soft.reclutamiento.entity.resources.Messages`
- **Equivalente .NET**: Acceso a archivos `.resx` (Resources.Common, Resources.Area, etc.)
- **Características**:
  - Component Spring que encapsula `MessageSource`
  - Métodos helper para mensajes comunes
  - Soporte para mensajes con parámetros
  - Locale automático desde `LocaleContextHolder`

**Métodos Principales**:
```java
// Obtener mensaje sin parámetros
public String get(String code)

// Obtener mensaje con parámetros
public String get(String code, Object[] args)

// Formatear mensaje (helper)
public String format(String code, Object... args)

// Mensajes comunes
public String createSuccess()
public String updateSuccess()
public String deleteSuccess()
public String recordNotFound()
public String deleteRecordNotFound()
public String fieldRequired(String fieldName)
public String fieldMinLength(String fieldName, int minLength)
public String fieldMaxLength(String fieldName, int maxLength)
public String emailInvalid(String fieldName)
public String identifierRequired()
public String informationRequired()
```

**Comparación .NET → Java**:
```csharp
// .NET
response.AddOkResult(Resources.Common.CreateSuccessMessage);
response.AddErrorResult(Resources.Common.RecordNotFound);
response.AddErrorResult(string.Format(Resources.Common.FieldRequired, "Nombre"));
```

```java
// Java
response.addOkResult(messages.createSuccess());
response.addErrorResult(messages.recordNotFound());
response.addErrorResult(messages.fieldRequired("Nombre"));
```

---

### 📄 Archivos de Mensajes

#### **common.properties**
- **Ubicación**: `resources/messages/common.properties`
- **Equivalente .NET**: `Resources.Common.resx`
- **Contenido**: Mensajes reutilizables para todas las entidades

```properties
# Success Messages
common.create.success=El registro fue creado correctamente
common.update.success=El registro fue actualizado correctamente
common.delete.success=El registro fue eliminado correctamente

# Error Messages
common.record.not.found=El registro no fue encontrado
common.delete.record.not.found=El registro a eliminar no fue encontrado
common.delete.reference.error=El registro no puede ser eliminado porque tiene registros relacionados
common.identifier.required=El identificador es requerido
common.information.required=La información a procesar es requerida

# Validation Messages
common.field.required=El campo {0} es requerido
common.field.min.length=El campo {0} debe tener al menos {1} caracteres
common.field.max.length=El campo {0} debe tener como máximo {1} caracteres
common.email.invalid=El campo {0} tiene un formato de email inválido

# Search/Query Messages
common.search.information.required=La información de búsqueda es requerida
common.search.page.information.required=La información de paginación es requerida
common.page.field=Página
common.page.size.field=Tamaño de página
common.page.field.min.value=El número de página debe ser mayor a 0
common.page.size.field.min.value=El tamaño de página debe ser mayor a 0
common.page.size.field.max.value=El tamaño de página no puede ser mayor a 1000
common.sort.direction.required=La dirección de ordenamiento es requerida
common.sort.direction.not.valid=La dirección de ordenamiento debe ser 'asc' o 'desc'
```

#### **area.properties**
- **Ubicación**: `resources/messages/area.properties`
- **Equivalente .NET**: `Resources.Area.resx`
- **Contenido**: Nombres de campos específicos de Area

```properties
area.field.idArea=IdArea
area.field.nombre=Nombre
area.field.activo=Activo
```

#### **MessageSourceConfig**
- **Ubicación**: `com.soft.reclutamiento.entity.config.MessageSourceConfig`
- **Función**: Configura Spring MessageSource para cargar archivos .properties
- **Características**:
  - Recarga automática de mensajes
  - Soporte para múltiples archivos
  - Encoding UTF-8

```java
@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
            new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                "classpath:messages/common",
                "classpath:messages/area"
                // Agregar más archivos según se necesiten
        );
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // 1 hora

        return messageSource;
    }
}
```

---

## 5. Mappers - MapStruct

### 📁 Mappers (`mappers/dbo/`)

#### **AreaMapper**
- **Ubicación**: `com.soft.reclutamiento.entity.mappers.dbo.AreaMapper`
- **Equivalente .NET**: Configuración de AutoMapper para Area
- **Características**:
  - Interface anotada con `@Mapper`
  - Generación automática de implementación en compile-time
  - Mapeo bidireccional Entity ↔ DTO

```java
@Mapper(componentModel = "spring")
public interface AreaMapper {

    // Entity → GetAreaDto
    GetAreaDto toGetAreaDto(Area area);

    // Entity → SearchAreaDto
    SearchAreaDto toSearchAreaDto(Area area);

    // Entity → SelectAreaDto
    SelectAreaDto toSelectAreaDto(Area area);

    // CreateAreaDto → Entity
    Area toEntity(CreateAreaDto dto);

    // UpdateAreaDto → Entity
    Area toEntity(UpdateAreaDto dto);
}
```

**Comparación .NET → Java**:
```csharp
// .NET - AutoMapper Profile
public class AreaMappingProfile : Profile
{
    public AreaMappingProfile()
    {
        CreateMap<Area, GetAreaDto>();
        CreateMap<Area, SearchAreaDto>();
        CreateMap<Area, SelectAreaDto>();
        CreateMap<CreateAreaDto, Area>();
        CreateMap<UpdateAreaDto, Area>();
    }
}
```

```java
// Java - MapStruct (más simple)
@Mapper(componentModel = "spring")
public interface AreaMapper {
    GetAreaDto toGetAreaDto(Area area);
    SearchAreaDto toSearchAreaDto(Area area);
    SelectAreaDto toSelectAreaDto(Area area);
    Area toEntity(CreateAreaDto dto);
    Area toEntity(UpdateAreaDto dto);
}
```

**Uso en Handlers**:
```java
@Component
public class CreateAreaCommandHandler
    extends CommandHandlerBaseWithResponse<CreateAreaCommand, GetAreaDto> {

    private final AreaMapper areaMapper;

    @Override
    protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
        var response = new ResponseDto<GetAreaDto>();

        // Usar mapper
        Area area = areaMapper.toEntity(command.getCreateDto());
        area = areaRepository.save(area);

        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);

        return response;
    }
}
```

---

## 6. Implementación de Area - Ejemplo Completo

### 📁 Commands Area (`commands/dbo/area/`)

#### **CreateAreaCommand**
```java
@Getter
@Setter
@AllArgsConstructor
public class CreateAreaCommand extends CommandWithResponse<GetAreaDto> {
    private CreateAreaDto createDto;
}
```

#### **CreateAreaCommandValidator**
```java
@Component
@RequiredArgsConstructor
public class CreateAreaCommandValidator extends CommandValidatorBase<CreateAreaCommand> {

    private final Validator validator;

    @Override
    public List<String> validate(CreateAreaCommand command) {
        var errors = createErrorList();

        if (command.getCreateDto() == null) {
            errors.add(messages.informationRequired());
            return errors;
        }

        // Jakarta Bean Validation
        Set<ConstraintViolation<Object>> violations = validator.validate(command.getCreateDto());
        for (ConstraintViolation<Object> violation : violations) {
            errors.add(violation.getMessage());
        }

        return errors;
    }
}
```

#### **CreateAreaCommandHandler**
```java
@Component
public class CreateAreaCommandHandler
    extends CommandHandlerBaseWithResponse<CreateAreaCommand, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public CreateAreaCommandHandler(
            Messages messages,
            CreateAreaCommandValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
        var response = new ResponseDto<GetAreaDto>();

        // Mapear y guardar
        Area area = areaMapper.toEntity(command.getCreateDto());
        area = areaRepository.save(area);

        // Mapear respuesta
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);
        response.addOkResult(messages.createSuccess());

        return response;
    }
}
```

**Estructura Similar para**:
- ✅ UpdateAreaCommand + Validator + Handler
- ✅ DeleteAreaCommand + Validator + Handler

---

### 📁 Queries Area (`queries/dbo/area/`)

#### **GetAreaQuery**
```java
@Getter
@Setter
@AllArgsConstructor
public class GetAreaQuery extends Query<GetAreaDto> {
    private Integer id;
}
```

#### **GetAreaQueryValidator**
```java
@Component
@RequiredArgsConstructor
public class GetAreaQueryValidator extends QueryValidatorBase<GetAreaQuery> {

    private final AreaRepository areaRepository;

    @Override
    public List<String> validate(GetAreaQuery query) {
        var errors = createErrorList();

        if (query.getId() == null) {
            errors.add(messages.identifierRequired());
            return errors;
        }

        // Validar que el Area exista
        var existingArea = areaRepository.findById(query.getId());
        if (existingArea.isEmpty()) {
            errors.add(messages.recordNotFound());
        }

        return errors;
    }
}
```

#### **GetAreaQueryHandler**
```java
@Component
public class GetAreaQueryHandler extends QueryHandlerBase<GetAreaQuery, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public GetAreaQueryHandler(
            Messages messages,
            GetAreaQueryValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleQuery(GetAreaQuery query) {
        var response = new ResponseDto<GetAreaDto>();

        // Obtener y mapear
        var area = areaRepository.findById(query.getId()).get();
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);

        return response;
    }
}
```

#### **SearchAreaQuery**
```java
public class SearchAreaQuery
    extends SearchQueryBase<SearchAreaFilterDto, SearchAreaDto> {

    public SearchAreaQuery(SearchParamsDto<SearchAreaFilterDto> searchParams) {
        super(searchParams);
    }
}
```

#### **SearchAreaQueryHandler**
```java
@Component
public class SearchAreaQueryHandler
    extends SearchQueryHandlerBase<SearchAreaQuery, SearchAreaFilterDto, SearchAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public SearchAreaQueryHandler(Messages messages,
                                   AreaRepository areaRepository,
                                   AreaMapper areaMapper) {
        super(messages); // Validación automática
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<SearchResultDto<SearchAreaDto>> handleQuery(SearchAreaQuery query) {
        // ... lógica de búsqueda completa mostrada anteriormente
    }
}
```

**Estructura Similar para**:
- ✅ SelectAreaQuery + Handler (para combos/selects)

---

## 7. Comparación Completa .NET vs Java

### Tabla de Equivalencias

| Concepto .NET | Concepto Java | Ubicación Java |
|---------------|---------------|----------------|
| `CommandBase` | `BaseCommand` | `commands.base.BaseCommand` |
| `CommandBase<TResponse>` | `CommandWithResponse<TResponse>` | `commands.base.CommandWithResponse` |
| `CommandValidatorBase<TRequest>` | `CommandValidatorBase<TCommand>` | `commands.base.CommandValidatorBase` |
| `CommandHandlerBase<TRequest>` | `CommandHandlerBase<TCommand>` | `commands.base.CommandHandlerBase` |
| `CommandHandlerBase<TRequest, TResponse>` | `CommandHandlerBaseWithResponse<TCommand, TResponse>` | `commands.base.CommandHandlerBaseWithResponse` |
| `QueryBase<TResponse>` | `Query<TResponse>` | `queries.base.Query` |
| `QueryValidatorBase<TRequest>` | `QueryValidatorBase<TQuery>` | `queries.base.QueryValidatorBase` |
| `QueryHandlerBase<TRequest, TResponse>` | `QueryHandlerBase<TQuery, TResponse>` | `queries.base.QueryHandlerBase` |
| `SearchQueryBase<TFilter, TResponse>` | `SearchQueryBase<TFilter, TResponse>` | `queries.base.SearchQueryBase` |
| `SearchQueryValidatorBase<...>` | `SearchQueryValidatorBase<...>` | `queries.base.SearchQueryValidatorBase` |
| `SearchQueryHandlerBase<...>` | `SearchQueryHandlerBase<...>` | `queries.base.SearchQueryHandlerBase` |
| `MediatR` | `PipelinR` | Dependencia externa |
| `FluentValidation` | `Jakarta Bean Validation + Custom Validators` | Dependencia externa + Custom |
| `AutoMapper` | `MapStruct` | Dependencia externa |
| `Resources.Common.resx` | `common.properties` | `resources/messages/common.properties` |
| `Resources.Area.resx` | `area.properties` | `resources/messages/area.properties` |
| `IUnitOfWork` | `@Transactional` (Spring) | Anotación de Spring |

---

### Ventajas de la Implementación Java

1. **Validación Automática**: Los handlers base llaman automáticamente a los validators
2. **Transacciones Declarativas**: `@Transactional` en lugar de IUnitOfWork manual
3. **Manejo de Excepciones Centralizado**: Las clases base capturan todas las excepciones
4. **SearchQuery con Validación Automática**: SearchQueryHandlerBase valida paginación/ordenamiento automáticamente
5. **Código Más Limpio**: Los handlers solo contienen lógica de negocio
6. **MapStruct en Compile-Time**: Mappers verificados en compilación (vs AutoMapper en runtime)
7. **Menos Código Repetitivo**: No necesitas llamar `validator.validate()` en cada handler

---

## 8. Flujo Completo de una Petición

### Ejemplo: Crear un Area

```
1. API Layer recibe POST /api/areas
   ↓
2. Controller crea CreateAreaCommand
   ↓
3. Controller llama: pipeline.send(command)
   ↓
4. PipelinR encuentra CreateAreaCommandHandler
   ↓
5. CreateAreaCommandHandler.handle() llamado
   ↓
6. CommandHandlerBaseWithResponse verifica command.isValidate()
   ↓
7. CommandHandlerBaseWithResponse llama validator.validate()
   ↓
8. CreateAreaCommandValidator valida:
   - DTO no null
   - Jakarta Bean Validation (@NotBlank, @Size)
   ↓
9. Si hay errores → retorna ResponseDto con errores
   ↓
10. Si no hay errores → llama handleCommand()
    ↓
11. handleCommand():
    - Mapea DTO → Entity (MapStruct)
    - Guarda en BD (JPA Repository)
    - Mapea Entity → DTO (MapStruct)
    - Retorna ResponseDto con data
    ↓
12. Controller retorna ResponseEntity<ResponseDto<GetAreaDto>>
```

### Ejemplo: Búsqueda de Areas

```
1. API Layer recibe POST /api/areas/search
   ↓
2. Controller crea SearchAreaQuery con SearchParamsDto
   ↓
3. Controller llama: pipeline.send(query)
   ↓
4. PipelinR encuentra SearchAreaQueryHandler
   ↓
5. SearchAreaQueryHandler.handle() llamado
   ↓
6. SearchQueryHandlerBase verifica si hay validator
   ↓
7. Si no hay validator → crea SearchQueryValidatorBase automáticamente
   ↓
8. SearchQueryValidatorBase valida:
   - SearchParams no null
   - Page no null
   - Page.page > 0
   - Page.pageSize entre 1-1000
   - Sort.direction en ["asc", "desc"]
   ↓
9. Si hay errores → retorna ResponseDto con errores
   ↓
10. Si no hay errores → llama handleQuery()
    ↓
11. handleQuery():
    - Construye JPA Specification (filtros dinámicos)
    - Construye SortExpression (ordenamiento)
    - Llama repository.searchByAsNoTracking()
    - Mapea List<Entity> → List<DTO> (MapStruct)
    - Retorna ResponseDto con SearchResultDto
    ↓
12. Controller retorna ResponseEntity<ResponseDto<SearchResultDto<SearchAreaDto>>>
```

---

## 9. Configuración Requerida

### pom.xml - Dependencias

```xml
<!-- PipelinR - CQRS/Mediator Pattern -->
<dependency>
    <groupId>net.sizovs</groupId>
    <artifactId>pipelinr</artifactId>
    <version>0.8</version>
</dependency>

<!-- Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

### pom.xml - Annotation Processors

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <!-- Lombok PRIMERO -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.30</version>
                    </path>
                    <!-- MapStruct SEGUNDO -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                    <!-- Binding TERCERO -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### PipelinR Configuration

```java
@Configuration
public class PipelinRConfiguration {

    @Bean
    Pipeline pipeline(ApplicationContext applicationContext) {
        return new Pipeline(applicationContext::getBean);
    }
}
```

---

## 10. Patrones y Mejores Prácticas

### ✅ DO - Hacer

1. **Extender siempre de clases base**
   ```java
   // ✅ CORRECTO
   public class CreateAreaCommand extends CommandWithResponse<GetAreaDto> { }

   // ❌ INCORRECTO
   public class CreateAreaCommand implements Command<ResponseDto<GetAreaDto>> { }
   ```

2. **Usar constructores explícitos en handlers**
   ```java
   // ✅ CORRECTO - Constructor explícito
   public CreateAreaCommandHandler(
           Messages messages,
           CreateAreaCommandValidator validator,
           AreaRepository areaRepository,
           AreaMapper areaMapper
   ) {
       super(messages, validator);
       this.areaRepository = areaRepository;
       this.areaMapper = areaMapper;
   }

   // ❌ INCORRECTO - @RequiredArgsConstructor no llama super()
   @RequiredArgsConstructor
   public class CreateAreaCommandHandler extends CommandHandlerBaseWithResponse<...> {
       // No funciona con herencia
   }
   ```

3. **Validadores siempre extendiendo de bases**
   ```java
   // ✅ CORRECTO
   @Component
   public class CreateAreaCommandValidator extends CommandValidatorBase<CreateAreaCommand> {
       @Override
       public List<String> validate(CreateAreaCommand command) { }
   }
   ```

4. **Usar Messages para todos los mensajes**
   ```java
   // ✅ CORRECTO
   response.addOkResult(messages.createSuccess());
   response.addErrorResult(messages.recordNotFound());

   // ❌ INCORRECTO
   response.addOkResult("Registro creado correctamente");
   ```

5. **SearchQuery sin validator personalizado**
   ```java
   // ✅ CORRECTO - Usa validación automática
   public SearchAreaQueryHandler(Messages messages,
                                  AreaRepository areaRepository,
                                  AreaMapper areaMapper) {
       super(messages); // Sin validator
   }
   ```

---

### ❌ DON'T - No Hacer

1. **No validar manualmente en handlers**
   ```java
   // ❌ INCORRECTO
   @Override
   protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
       var response = new ResponseDto<GetAreaDto>();

       // ❌ NO HACER - validación manual
       if (command.isValidate()) {
           List<String> errors = validator.validate(command);
           if (!errors.isEmpty()) {
               errors.forEach(response::addErrorResult);
               return response;
           }
       }

       // lógica...
   }
   ```
   **Por qué**: CommandHandlerBase ya hace esto automáticamente

2. **No usar try-catch en handlers**
   ```java
   // ❌ INCORRECTO
   @Override
   protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
       var response = new ResponseDto<GetAreaDto>();

       try {
           // lógica...
       } catch (Exception e) {
           response.addErrorResult("Error: " + e.getMessage());
       }

       return response;
   }
   ```
   **Por qué**: Las clases base ya manejan excepciones

3. **No implementar Command.Handler directamente**
   ```java
   // ❌ INCORRECTO
   public class CreateAreaCommandHandler
       implements Command.Handler<CreateAreaCommand, ResponseDto<GetAreaDto>> {
       // Pierdes toda la funcionalidad de las clases base
   }
   ```

4. **No mezclar lógica de validación en handlers**
   ```java
   // ❌ INCORRECTO
   @Override
   protected ResponseDto handleCommand(DeleteAreaCommand command) {
       var response = new ResponseDto();

       // ❌ NO validar en handler
       if (command.getId() == null) {
           response.addErrorResult("ID requerido");
           return response;
       }

       // lógica...
   }
   ```
   **Por qué**: Las validaciones van en Validators

---

## 11. Resumen de Archivos Creados

### Clases Base

```
reclutamiento-entity/
├── commands/base/
│   ├── BaseCommand.java                           ✅ (ya existía)
│   ├── CommandWithResponse.java                   ✅ (ya existía)
│   ├── CommandValidatorBase.java                  ✅ NUEVO
│   ├── CommandHandlerBase.java                    ✅ NUEVO
│   └── CommandHandlerBaseWithResponse.java        ✅ NUEVO
├── queries/base/
│   ├── Query.java                                 ✅ (ya existía)
│   ├── QueryValidatorBase.java                   ✅ NUEVO
│   ├── QueryHandlerBase.java                     ✅ NUEVO
│   ├── SearchQueryBase.java                      ✅ NUEVO
│   ├── SearchQueryValidatorBase.java             ✅ NUEVO
│   └── SearchQueryHandlerBase.java               ✅ NUEVO
├── resources/
│   └── Messages.java                              ✅ ACTUALIZADO (nuevos métodos)
└── config/
    └── MessageSourceConfig.java                   ✅ NUEVO
```

### Archivos de Mensajes

```
reclutamiento-entity/
└── src/main/resources/messages/
    ├── common.properties                          ✅ ACTUALIZADO
    └── area.properties                            ✅ (ya existía)
```

### Implementación Area - Refactorizada

```
reclutamiento-entity/
├── commands/dbo/area/
│   ├── CreateAreaCommand.java                     ✅ (sin cambios)
│   ├── CreateAreaCommandValidator.java            ✅ REFACTORIZADO
│   ├── CreateAreaCommandHandler.java              ✅ REFACTORIZADO
│   ├── UpdateAreaCommand.java                     ✅ (sin cambios)
│   ├── UpdateAreaCommandValidator.java            ✅ REFACTORIZADO
│   ├── UpdateAreaCommandHandler.java              ✅ REFACTORIZADO
│   ├── DeleteAreaCommand.java                     ✅ (sin cambios)
│   ├── DeleteAreaCommandValidator.java            ✅ REFACTORIZADO
│   └── DeleteAreaCommandHandler.java              ✅ REFACTORIZADO
└── queries/dbo/area/
    ├── GetAreaQuery.java                          ✅ (sin cambios)
    ├── GetAreaQueryValidator.java                 ✅ REFACTORIZADO
    ├── GetAreaQueryHandler.java                   ✅ REFACTORIZADO
    ├── SearchAreaQuery.java                       ✅ REFACTORIZADO
    ├── SearchAreaQueryHandler.java                ✅ REFACTORIZADO
    ├── SelectAreaQuery.java                       ✅ REFACTORIZADO
    └── SelectAreaQueryHandler.java                ✅ REFACTORIZADO
```

---

## 12. Próximos Pasos

1. ✅ **Commands y Queries Base** - COMPLETADO
2. ✅ **Validators Base** - COMPLETADO
3. ✅ **Handlers Base con validación automática** - COMPLETADO
4. ✅ **Resources e i18n** - COMPLETADO
5. ✅ **Refactorización de Area** - COMPLETADO
6. ⏳ **Implementar otras entidades** (Persona, Postulante, etc.)
7. ⏳ **Capa API - Controllers RESTful**
8. ⏳ **Seguridad y JWT**
9. ⏳ **Tests unitarios e integración**

---

## 13. Contacto y Soporte

Para dudas o mejoras en la implementación:
- Revisar documentación de PipelinR: https://github.com/sizovs/pipelinr
- Revisar documentación de MapStruct: https://mapstruct.org/
- Consultar código .NET original

---

**Fecha de Documentación**: 2025-11-06
**Versión**: 1.0
**Estado**: ✅ Implementación Completa
