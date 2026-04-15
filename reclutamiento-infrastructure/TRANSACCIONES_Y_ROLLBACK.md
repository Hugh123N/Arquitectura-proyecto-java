
## 🎯 Resumen

Implementación del patrón Unit of Work con manejo automático de transacciones, commit y rollback.

---

## 📋 Componentes Implementados

### 1. ITransaction
Interface para manejo de transacciones individuales.

**Métodos:**
- `getTransactionId()` - ID único de la transacción
- `commit()` - Confirma la transacción
- `rollback()` - Revierte la transacción
- `close()` - Cierra la transacción (rollback automático si no fue completada)

### 2. TransactionImpl
Implementación de ITransaction que envuelve `Spring TransactionStatus`.

### 3. IUnitOfWork
Interface para el patrón Unit of Work.

**Métodos:**
- `beginTransaction()` - Inicia nueva transacción
- `getCurrentTransaction()` - Obtiene transacción actual
- `execute(operation)` - Ejecuta con commit/rollback automático
- `executeInTransaction(operation)` - Ejecuta dentro de transacción explícita
- `commit()` - Guarda cambios pendientes
- `clearChangeTracker()` - Limpia el EntityManager

### 4. UnitOfWorkImpl
Implementación concreta del Unit of Work.

---

## 🔄 Cómo Funciona el Rollback Automático

### Escenario 1: Operación Normal (Sin Errores)

```java
@Service
public class ProveedorService {

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private ProveedorRepository proveedorRepository;

    public Proveedor crearProveedor(ProveedorDto dto) {
        return unitOfWork.executeInTransaction(() -> {
            // 1. Crear proveedor
            Proveedor proveedor = new Proveedor();
            proveedor.setRazonSocial(dto.getRazonSocial());
            proveedor.setRuc(dto.getRuc());

            // 2. Guardar (parte de la transacción)
            proveedorRepository.save(proveedor);

            // 3. Si todo sale bien, hace COMMIT automáticamente
            return proveedor;
        });
        // ✅ COMMIT automático aquí si no hubo errores
    }
}
```

**Flujo:**
```
BEGIN TRANSACTION
  → INSERT INTO Proveedor (...)
  → [Sin errores]
  → COMMIT ✅
```

---

### Escenario 2: Error Durante la Operación (Rollback Automático)

```java
@Service
public class ProveedorService {

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    public Proveedor crearProveedorConContacto(ProveedorDto dto) {
        return unitOfWork.executeInTransaction(() -> {
            // 1. Crear y guardar proveedor
            Proveedor proveedor = new Proveedor();
            proveedor.setRazonSocial(dto.getRazonSocial());
            proveedorRepository.save(proveedor);  // ✅ INSERT exitoso

            // 2. Crear contacto con datos inválidos
            Contacto contacto = new Contacto();
            contacto.setEmail("email-invalido");  // ❌ Email inválido
            contactoRepository.save(contacto);     // ❌ Lanza excepción

            return proveedor;
        });
        // ❌ ROLLBACK automático aquí por la excepción
    }
}
```

**Flujo:**
```
BEGIN TRANSACTION
  → INSERT INTO Proveedor (...)  ✅ Exitoso (en memoria)
  → INSERT INTO Contacto (...)   ❌ Error (excepción lanzada)
  → ROLLBACK ⚠️ (deshace el INSERT de Proveedor)
END TRANSACTION
```

**Resultado:** El Proveedor NO queda guardado en la BD, ambas operaciones se revierten.

---

### Escenario 3: Múltiples Operaciones con Rollback Parcial

```java
@Service
public class OrdenCompraService {

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private OrdenCompraRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleRepository;

    @Autowired
    private InventarioService inventarioService;

    public OrdenCompra crearOrdenCompra(OrdenCompraDto dto) {
        return unitOfWork.executeInTransaction(() -> {
            // 1. Crear orden
            OrdenCompra orden = new OrdenCompra();
            orden.setNumero(dto.getNumero());
            orden = ordenRepository.save(orden);  // ✅ INSERT exitoso

            // 2. Guardar detalles
            for (DetalleDto detalleDto : dto.getDetalles()) {
                DetalleOrden detalle = new DetalleOrden();
                detalle.setOrden(orden);
                detalle.setProducto(detalleDto.getProducto());
                detalle.setCantidad(detalleDto.getCantidad());
                detalleRepository.save(detalle);  // ✅ INSERT exitoso
            }

            // 3. Actualizar inventario (puede fallar)
            inventarioService.descontarStock(dto.getDetalles());  // ❌ Stock insuficiente!

            return orden;
        });
        // ❌ ROLLBACK: Se deshacen TODOS los cambios (orden + detalles)
    }
}
```

**Flujo:**
```
BEGIN TRANSACTION
  → INSERT INTO OrdenCompra (...)       ✅ En memoria
  → INSERT INTO DetalleOrden (...)      ✅ En memoria
  → INSERT INTO DetalleOrden (...)      ✅ En memoria
  → UPDATE Inventario SET stock = ...   ❌ Stock insuficiente (excepción)
  → ROLLBACK ⚠️ (deshace TODO)
END TRANSACTION
```

---

## 🎓 Uso con @Transactional de Spring

### Opción 1: Usar IUnitOfWork (Recomendado para lógica compleja)

```java
@Service
public class MiServicio {

    @Autowired
    private IUnitOfWork unitOfWork;

    public Result realizarOperacionCompleja() {
        return unitOfWork.executeInTransaction(() -> {
            // Lógica de negocio aquí
            // Rollback automático si hay excepción
            return result;
        });
    }
}
```

**Ventajas:**
- ✅ Control explícito de transacciones
- ✅ Anidamiento de transacciones
- ✅ Compatibilidad con patrón .NET

---

### Opción 2: Usar @Transactional de Spring (Recomendado para operaciones simples)

```java
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Transactional  // ← Spring maneja commit/rollback automáticamente
    public Proveedor crearProveedor(ProveedorDto dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(dto.getRazonSocial());

        // Si esta línea lanza excepción, Spring hace ROLLBACK automático
        return proveedorRepository.save(proveedor);

        // Si no hay excepción, Spring hace COMMIT automático
    }
}
```

**Ventajas:**
- ✅ Más simple y declarativo
- ✅ Estándar de Spring
- ✅ Rollback automático en excepciones unchecked

---

## ⚠️ Manejo de Excepciones

### Rollback en Excepciones Checked vs Unchecked

#### Con @Transactional (Spring):

```java
@Service
public class MiServicio {

    // Rollback SOLO en RuntimeException (unchecked)
    @Transactional
    public void metodo1() throws Exception {
        // ...
        throw new Exception("Error checked");  // ❌ NO hace rollback por defecto
    }

    // Rollback en RuntimeException
    @Transactional
    public void metodo2() {
        // ...
        throw new RuntimeException("Error");   // ✅ SÍ hace rollback
    }

    // Rollback en todas las excepciones
    @Transactional(rollbackFor = Exception.class)
    public void metodo3() throws Exception {
        // ...
        throw new Exception("Error");          // ✅ SÍ hace rollback
    }
}
```

#### Con IUnitOfWork:

```java
@Service
public class MiServicio {

    @Autowired
    private IUnitOfWork unitOfWork;

    public void metodo() {
        unitOfWork.executeInTransaction(() -> {
            // ...
            throw new Exception("Error");  // ✅ SÍ hace rollback (cualquier excepción)
        });
    }
}
```

---

## 🔍 Debugging de Transacciones

### Ver transacciones en logs:

```properties
# application.properties
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.orm.jpa=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Salida esperada:**
```
DEBUG o.s.orm.jpa.JpaTransactionManager : Creating new transaction
DEBUG o.s.orm.jpa.JpaTransactionManager : Opened new EntityManager
DEBUG org.hibernate.SQL : INSERT INTO Proveedor (...)
DEBUG o.s.orm.jpa.JpaTransactionManager : Committing JPA transaction
DEBUG o.s.orm.jpa.JpaTransactionManager : Closing JPA EntityManager
```

---

## 🆚 Comparación .NET vs Java

| Concepto | .NET (EF Core) | Java (Spring/JPA) |
|----------|----------------|-------------------|
| Transaction | `IDbContextTransaction` | `TransactionStatus` |
| Begin Transaction | `context.Database.BeginTransaction()` | `transactionManager.getTransaction()` |
| Commit | `transaction.Commit()` | `transactionManager.commit()` |
| Rollback | `transaction.Rollback()` | `transactionManager.rollback()` |
| CancellationToken | `CancellationToken` | No existe (usar timeout) |
| Unit of Work | `IUnitOfWork` | `IUnitOfWork` (implementado) |
| Clear Tracker | `context.ChangeTracker.Clear()` | `entityManager.clear()` |
| Declarativo | No tiene | `@Transactional` |

---

## 📝 Ejemplos Completos

### Ejemplo 1: Crear Proveedor con Contactos (Todo o Nada)

```java
@Service
public class ProveedorService {

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    public Proveedor crearProveedorConContactos(CrearProveedorRequest request) {
        return unitOfWork.executeInTransaction(() -> {
            // 1. Crear proveedor
            Proveedor proveedor = new Proveedor();
            proveedor.setRazonSocial(request.getRazonSocial());
            proveedor.setRuc(request.getRuc());
            proveedor = proveedorRepository.save(proveedor);

            // 2. Crear contactos
            for (ContactoDto contactoDto : request.getContactos()) {
                Contacto contacto = new Contacto();
                contacto.setProveedor(proveedor);
                contacto.setNombre(contactoDto.getNombre());
                contacto.setEmail(contactoDto.getEmail());
                contacto.setTelefono(contactoDto.getTelefono());

                // Validar email (puede lanzar excepción)
                if (!isValidEmail(contacto.getEmail())) {
                    throw new ValidationException("Email inválido: " + contacto.getEmail());
                    // ← Si llega aquí, ROLLBACK automático
                    // ← El proveedor NO quedará guardado
                }

                contactoRepository.save(contacto);
            }

            return proveedor;
            // ← Si llegó aquí sin excepciones, COMMIT automático
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

---

### Ejemplo 2: Operación con Rollback Manual

```java
@Service
public class PagoService {

    @Autowired
    private IUnitOfWork unitOfWork;

    public ResultadoPago procesarPago(PagoDto pagoDto) {
        ITransaction transaction = unitOfWork.beginTransaction();

        try {
            // 1. Registrar pago
            Pago pago = new Pago();
            pago.setMonto(pagoDto.getMonto());
            pagoRepository.save(pago);

            // 2. Llamar a API externa de banco
            RespuestaBanco respuesta = bancoService.procesarPago(pagoDto);

            if (!respuesta.isExitoso()) {
                // ❌ Pago rechazado por el banco
                transaction.rollback();  // ← Rollback manual
                unitOfWork.clearChangeTracker();
                return ResultadoPago.rechazado(respuesta.getMensaje());
            }

            // 3. Actualizar estado del pago
            pago.setEstado("APROBADO");
            pago.setCodigoTransaccion(respuesta.getCodigoTransaccion());
            pagoRepository.save(pago);

            // 4. Commit manual
            transaction.commit();  // ← Commit manual

            return ResultadoPago.exitoso(pago);

        } catch (Exception e) {
            // ❌ Error inesperado
            transaction.rollback();
            unitOfWork.clearChangeTracker();
            throw e;
        } finally {
            transaction.close();  // ← Asegura que se cierre la transacción
        }
    }
}
```

---

## ✅ Mejores Prácticas

1. **Usar @Transactional para operaciones simples:**
   ```java
   @Transactional
   public void operacionSimple() { ... }
   ```

2. **Usar IUnitOfWork para operaciones complejas:**
   ```java
   unitOfWork.executeInTransaction(() -> { ... });
   ```

3. **Siempre usar try-catch-finally con transacciones manuales:**
   ```java
   try (ITransaction tx = unitOfWork.beginTransaction()) {
       // operación
       tx.commit();
   } catch (Exception e) {
       // rollback automático en close()
   }
   ```

4. **Limpiar el change tracker después de rollback:**
   ```java
   catch (Exception e) {
       transaction.rollback();
       unitOfWork.clearChangeTracker();  // ← Importante
   }
   ```

5. **No capturar excepciones sin re-lanzarlas (rompe el rollback):**
   ```java
   // ❌ MAL
   @Transactional
   public void metodo() {
       try {
           repository.save(entity);
       } catch (Exception e) {
           // Se traga la excepción → NO hace rollback!
       }
   }

   // ✅ BIEN
   @Transactional
   public void metodo() {
       try {
           repository.save(entity);
       } catch (Exception e) {
           log.error("Error", e);
           throw e;  // ← Re-lanzar para que haga rollback
       }
   }
   ```

---

## 🔧 Configuración

### Habilitar logs de transacciones:

```properties
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG
```

---

## 📊 Resumen

| Característica | Estado |
|---------------|--------|
| ✅ ITransaction | Implementado |
| ✅ TransactionImpl | Implementado |
| ✅ IUnitOfWork | Implementado |
| ✅ UnitOfWorkImpl | Implementado |
| ✅ Rollback automático en excepciones | Implementado |
| ✅ Commit automático | Implementado |
| ✅ Transacciones anidadas | Implementado |
| ✅ Clear change tracker | Implementado |
| ⚠️ CancellationToken | No existe en Java (usar timeout) |

---

Generado: 2025-11-05
Autor: Claude Code
Proyecto: Reclutamiento - Migración .NET a Java/Spring Boot
