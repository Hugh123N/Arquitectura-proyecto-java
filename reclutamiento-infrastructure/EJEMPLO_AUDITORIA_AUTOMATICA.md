# Ejemplo: Auditoría Automática con JPA

## 📌 Caso de Uso: Entidad Proveedor

### 1️⃣ Definir la Entidad

```java
package com.soft.reclutamiento.entity.entities;

import com.soft.reclutamiento.entity.base.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "Proveedor")
public class Proveedor extends AuditableEntity {  // ← Extiende AuditableEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProveedor")
    private Long idProveedor;

    @Column(name = "idUsuario")
    private String idUsuario;

    @Column(name = "razonSocial", length = 255)
    private String razonSocial;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "idTipoProveedor")
    private Long idTipoProveedor;

    @Column(name = "idEstadoProveedor")
    private Long idEstadoProveedor;

    // Getters y Setters
    // Los campos de auditoría vienen heredados de AuditableEntity:
    // - userNameCreate
    // - createDate
    // - userNameUpdate
    // - updateDate
    // - activo
}
```

### 2️⃣ Crear el Repository

```java
package com.soft.reclutamiento.infrastructure.repository;

import com.soft.reclutamiento.entity.entities.Proveedor;
import com.soft.reclutamiento.infrastructure.repository.base.IBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends IBaseRepository<Proveedor, Long> {
    // Ya hereda todos los métodos CRUD + los personalizados
}
```

### 3️⃣ Usar en el Servicio/Handler

#### Ejemplo 1: CREAR un nuevo Proveedor

```java
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public Proveedor crearProveedor(ProveedorDto dto) {
        // 1. Crear la entidad
        Proveedor proveedor = new Proveedor();
        proveedor.setIdUsuario(dto.getIdUsuario());
        proveedor.setRazonSocial(dto.getRazonSocial());
        proveedor.setRuc(dto.getRuc());
        proveedor.setIdTipoProveedor(dto.getIdTipoProveedor());
        proveedor.setIdEstadoProveedor(dto.getIdEstadoProveedor());

        // NO necesitas setear estos campos, JPA lo hace automáticamente:
        // ❌ proveedor.setUserNameCreate("...");  // JPA lo llena automáticamente
        // ❌ proveedor.setCreateDate(...);         // JPA lo llena automáticamente
        // ❌ proveedor.setActivo(true);            // Ya está en true por defecto

        // 2. Guardar (aquí se activa la auditoría automática)
        Proveedor saved = proveedorRepository.save(proveedor);

        // 3. Después del save, la entidad ya tiene los campos llenos:
        System.out.println("Creado por: " + saved.getUserNameCreate());     // → "system"
        System.out.println("Fecha creación: " + saved.getCreateDate());     // → 2025-11-05T00:30:00
        System.out.println("Activo: " + saved.getActivo());                 // → true
        System.out.println("Actualizado por: " + saved.getUserNameUpdate()); // → null
        System.out.println("Fecha update: " + saved.getUpdateDate());       // → null

        return saved;
    }
}
```

**SQL generado por JPA (automáticamente):**
```sql
INSERT INTO Proveedor (
    idUsuario,
    razonSocial,
    ruc,
    idTipoProveedor,
    idEstadoProveedor,
    userNameCreate,      -- ✅ JPA lo llena con "system"
    createDate,          -- ✅ JPA lo llena con CURRENT_TIMESTAMP
    activo               -- ✅ JPA lo llena con true
) VALUES (
    'USR123',
    'Mi Empresa SAC',
    '20123456789',
    1,
    1,
    'system',            -- ← Llenado por @CreatedBy
    '2025-11-05T00:30:00', -- ← Llenado por @CreatedDate
    1                    -- ← Llenado por @PrePersist
)
```

#### Ejemplo 2: ACTUALIZAR un Proveedor existente

```java
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public Proveedor actualizarProveedor(Long id, ProveedorDto dto) {
        // 1. Obtener la entidad existente
        Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // 2. Modificar los campos
        proveedor.setRazonSocial(dto.getRazonSocial());
        proveedor.setRuc(dto.getRuc());

        // NO necesitas setear estos campos, JPA lo hace automáticamente:
        // ❌ proveedor.setUserNameUpdate("...");  // JPA lo llena automáticamente
        // ❌ proveedor.setUpdateDate(...);         // JPA lo llena automáticamente

        // 3. Guardar (aquí se activa la auditoría automática)
        Proveedor updated = proveedorRepository.save(proveedor);

        // 4. Después del save, la entidad ya tiene los campos actualizados:
        System.out.println("Creado por: " + updated.getUserNameCreate());     // → "system" (NO CAMBIA)
        System.out.println("Fecha creación: " + updated.getCreateDate());     // → 2025-11-05T00:30:00 (NO CAMBIA)
        System.out.println("Actualizado por: " + updated.getUserNameUpdate()); // → "system" (NUEVO)
        System.out.println("Fecha update: " + updated.getUpdateDate());       // → 2025-11-05T01:45:00 (NUEVO)
        System.out.println("Activo: " + updated.getActivo());                 // → true

        return updated;
    }
}
```

**SQL generado por JPA (automáticamente):**
```sql
UPDATE Proveedor SET
    razonSocial = 'Mi Empresa SAC - MODIFICADO',
    ruc = '20987654321',
    userNameUpdate = 'system',        -- ✅ JPA lo llena con "system"
    updateDate = '2025-11-05T01:45:00' -- ✅ JPA lo llena con CURRENT_TIMESTAMP
WHERE idProveedor = 1

-- NOTA: userNameCreate y createDate NO están en el UPDATE
-- porque tienen 'updatable = false'
```

---

## 🎯 Campos de Auditoría por Operación

| Campo | CREATE (INSERT) | UPDATE | Notas |
|-------|----------------|--------|-------|
| `userNameCreate` | ✅ Se llena | 🔒 **NO cambia** | `updatable = false` |
| `createDate` | ✅ Se llena | 🔒 **NO cambia** | `updatable = false` |
| `userNameUpdate` | ❌ NULL | ✅ Se llena | Solo en updates |
| `updateDate` | ❌ NULL | ✅ Se llena | Solo en updates |
| `activo` | ✅ `true` | ✅ Se mantiene | Default en @PrePersist |

---

## 🔧 Anotaciones JPA Usadas

### `@CreatedBy`
```java
@CreatedBy
@Column(name = "userNameCreate", length = 250, updatable = false)
private String userNameCreate;
```
- **Cuándo se llena**: Solo al crear (INSERT)
- **Quién lo llena**: `AuditorAware<String>` (bean `auditorProvider`)
- **Valor**: "system" (por ahora, luego será el username del JWT)

### `@CreatedDate`
```java
@CreatedDate
@Column(name = "createDate", updatable = false)
private OffsetDateTime createDate;
```
- **Cuándo se llena**: Solo al crear (INSERT)
- **Quién lo llena**: JPA automáticamente
- **Valor**: Timestamp actual

### `@LastModifiedBy`
```java
@LastModifiedBy
@Column(name = "userNameUpdate", length = 250)
private String userNameUpdate;
```
- **Cuándo se llena**: En cada update (UPDATE)
- **Quién lo llena**: `AuditorAware<String>` (bean `auditorProvider`)
- **Valor**: "system" (por ahora, luego será el username del JWT)

### `@LastModifiedDate`
```java
@LastModifiedDate
@Column(name = "updateDate")
private OffsetDateTime updateDate;
```
- **Cuándo se llena**: En cada update (UPDATE)
- **Quién lo llena**: JPA automáticamente
- **Valor**: Timestamp actual

### `@PrePersist`
```java
@PrePersist
protected void onCreate() {
    if (activo == null) {
        activo = true;
    }
}
```
- **Cuándo se ejecuta**: Antes de persistir (INSERT)
- **Qué hace**: Asegura que `activo` sea `true` si es null

---

## 🔑 Configuración Necesaria

### Ya está configurado en `JpaAuditingConfig.java`:

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider(IUserIdentity userIdentity) {
        return () -> {
            try {
                String currentUser = userIdentity.getCurrentUser();
                return Optional.ofNullable(currentUser);
            } catch (Exception e) {
                return Optional.of("system"); // ← Por ahora retorna "system"
            }
        };
    }
}
```

**Cuando implementes JWT:**
1. Descomentar `UserIdentityImpl.java`
2. El `auditorProvider` automáticamente usará el username del token JWT
3. Los campos `userNameCreate` y `userNameUpdate` tendrán el usuario real

---

## ✅ Casos Especiales

### Caso 1: Entidad con solo `activo`

Si una entidad solo tiene el campo `activo` (sin campos de auditoría de fechas):

```java
@Entity
@Table(name = "Categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo = true;  // ← Simplemente esto, sin heredar AuditableEntity

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
    }
}
```

### Caso 2: Entidad con auditoría completa

Para entidades que necesitan auditoría completa:

```java
@Entity
@Table(name = "Proveedor")
public class Proveedor extends AuditableEntity {  // ← Hereda todos los campos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProveedor;

    // ... otros campos

    // Ya tiene automáticamente:
    // - userNameCreate
    // - createDate
    // - userNameUpdate
    // - updateDate
    // - activo
}
```

---

## 🎓 Resumen

### ✅ LO QUE YA ESTÁ HECHO:
1. ✅ Clase base `AuditableEntity` con todos los campos
2. ✅ Anotaciones JPA configuradas (@CreatedBy, @CreatedDate, etc.)
3. ✅ Configuración de JPA Auditing habilitada
4. ✅ Bean `auditorProvider` configurado
5. ✅ Campo `activo` con valor default `true`
6. ✅ Campos de creación con `updatable = false`

### 🎯 LO QUE DEBES HACER:
1. **Heredar de AuditableEntity** en tus entidades que necesiten auditoría:
   ```java
   public class MiEntidad extends AuditableEntity {
   ```

2. **¡Y YA!** JPA hace el resto automáticamente. No necesitas:
   - ❌ Setear manualmente `userNameCreate`
   - ❌ Setear manualmente `createDate`
   - ❌ Setear manualmente `userNameUpdate`
   - ❌ Setear manualmente `updateDate`
   - ❌ Setear manualmente `activo` (ya está en true)

### 🔮 FUTURO (cuando agregues JWT):
- Descomentar `UserIdentityImpl.java`
- Los campos `userNameCreate` y `userNameUpdate` tendrán el username real del token JWT
- El resto sigue funcionando igual

---

¿Ahora quedó más claro cómo funciona la auditoría automática? 🎯
