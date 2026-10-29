# Informe Técnico de Arquitectura e Implementación: GestaMercado Backend API

**Proyecto:** GestaMercado RESTful API (Gobierno de Chiapas)  
**Versión:** 1.0.0-SNAPSHOT  
**Stack Principal:** Java 21 LTS | Spring Boot 3.3.5 | PostgreSQL 18 + PostGIS | Hibernate Spatial | Argon2id | Spring Security + JJWT  

---

## 1. Resumen Ejecutivo del Proyecto

**GestaMercado** es una plataforma tecnológica gubernamental diseñada para la gestión integral de mercados públicos en el Estado de Chiapas. El backend proporciona una API RESTful de alta seguridad y rendimiento para la administración del padrón comercial, control de titularidad de concesiones, gestión de asistencias, fiscalización de tarifas con cálculo automatizado de recargos, cobro en campo con sincronización offline criptográfica, y liquidación de ingresos a la Secretaría de Hacienda.

---

## 2. Decisiones Arquitectónicas: ¿Cómo se hizo y Por qué se hizo así?

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Capas de la Aplicación                          │
├────────────────────────────────────────────────────────────────────────┤
│  1. Controladores REST (Spring MVC + OpenAPI 3 / Swagger)              │
│  2. Seguridad y Filtros (JwtAuthenticationFilter + Argon2id)           │
│  3. Servicios de Dominio (Transacciones de Negocio)                    │
│  4. Aspecto RLS (Spring AOP -> Inyección de Firma HMAC en PostgreSQL)  │
│  5. Capa de Datos Híbrida:                                             │
│     ├── Spring Data JPA + Hibernate Spatial (Entidades & GeoJSON)      │
│     └── Spring JdbcClient (Invocación a Procedimientos PL/pgSQL)       │
│  6. Base de Datos (PostgreSQL 18: PostGIS, Citext, Pgcrypto, RLS)      │
└────────────────────────────────────────────────────────────────────────┘
```

### 2.1. Arquitectura en Capas y Separación de Responsabilidades
* **¿Cómo se hizo?** Se utilizó una arquitectura orientada al dominio (*Domain-Driven Package Layout*): `acceso`, `catalogos`, `padron`, `titularidad`, `fiscal`, `cobranza` y `reportes`. Cada dominio encapsula sus propios `Controller`, `Service`, `Repository`, `Dto`, `Entity` y `Mapper`.
* **¿Por qué se hizo así?** Permite alta cohesión y bajo acoplamiento. En sistemas con más de 250 clases, evita clases gigantescas y facilita que diferentes módulos (como fiscalización o cobro en campo) evolucionen independientemente sin afectar el núcleo del sistema.

### 2.2. Capa de Persistencia y ORM Híbrido (JPA + JDBC)
* **¿Cómo se hizo?**
  1. **Spring Data JPA con Hibernate 6.5**: Se usa para el modelado declarativo de relaciones (`@Entity`, `@ManyToOne`, `@OneToMany`), auditoría de auditoría y operaciones estándar CRUD.
  2. **Hibernate Spatial + JTS (Java Topology Suite 1.19)**: Modela geometrías espaciales en el estándar SRID 4326 (WGS84). Permite representar polígonos de zonificación de mercados y coordenadas GPS puntuales de locales y cobradores.
  3. **Spring `JdbcClient` / Stored Procedures**: Para las operaciones críticas de negocio (como `gesta.fn_registrar_cobro_campo` o `gesta.fn_calcular_cuota`), se invoca directamente código PL/pgSQL en la base de datos mediante JDBC.
* **¿Por qué se hizo así?** Un ORM puro no es eficiente para cálculos fiscales pesados ni para validaciones de hashes criptográficos sobre grandes lotes. Al delegar la matemática fiscal y la concurrencia a funciones almacenadas de PostgreSQL mientras el ORM maneja las entidades del dominio, se obtiene lo mejor de ambos mundos: **productividad en el código y máximo rendimiento en el motor**.

### 2.3. Transformación de Datos con MapStruct
* **¿Cómo se hizo?** Todas las conversiones entre entidades JPA y DTOs se realizan a través de interfaces anotadas con `@Mapper(componentModel = "spring")`.
* **¿Por qué se hizo así?** Se rechazó el uso de librerías basadas en reflexión en tiempo de ejecución (como ModelMapper). **MapStruct genera código Java nativo en tiempo de compilación**, garantizando rendimiento extremo (cero sobrecarga de CPU), detección inmediata de errores de tipado durante `mvn compile` y seguridad en tipos nulos.

---

## 3. Arquitectura de Seguridad Profunda (Defense in Depth)

La seguridad no se limitó a un simple login; se implementaron **cuatro barreras de seguridad complementarias**:

### 3.1. Hashing Criptográfico con Argon2id
* **Detalle técnico:** En lugar de algoritmos superados como MD5/SHA o antiguos como BCrypt, se configuró **Argon2id** (ganador del *Password Hashing Competition*) utilizando la librería **Bouncy Castle (`bcprov-jdk18on`)**.
* **Parámetros configurados:**
  - `Salt Length`: 16 bytes.
  - `Hash Length`: 32 bytes.
  - `Memory`: 64 MB (65,536 KB) - *Memory-hard para neutralizar ataques por hardware dedicado (GPU/ASIC)*.
  - `Iterations`: 3 iteraciones de cómputo.
  - `Parallelism`: 1 hilo por hash.

### 3.2. Tokens JWT de Doble Factor (Access Token + Refresh Token Rotativo)
* **Access Token:** Tiempo de vida corto (30 minutos) firmado con clave HMAC-SHA256 (`jjwt 0.12.6`). Contiene la identidad, rol y UUID del usuario.
* **Refresh Token:** Duración de 7 días. Se persiste en la base de datos (`gesta.sesion_refresh`). Cada vez que se solicita un nuevo access token, el refresh token se **invalida y se rota**, evitando ataques de repetición o robo de tokens de sesión.

### 3.3. Row Level Security (RLS) Coordinado entre Spring Boot y PostgreSQL
* **¿Por qué es especial?** En la mayoría de proyectos universitarios, si un usuario hackea un parámetro en el backend, puede ver datos de otro mercado. En GestaMercado **esto es matemáticamente imposible** gracias a RLS.
* **Mecanismo:**
  1. En PostgreSQL, las tablas (`local_comercial`, `cargo`, `pago`) tienen habilitado `ROW LEVEL SECURITY`.
  2. Spring Boot intercepta cada llamada transaccional mediante el aspecto [RlsTransactionAspect](file:///c:/Users/kaled/Downloads/Gestamercado/src/main/java/mx/gob/chiapas/gestamercado/api/config/RlsTransactionAspect.java).
  3. El aspecto calcula un hash criptográfico HMAC con un secreto simétrico de 32 bytes (`gesta.secreto_firma_sesion`) y ejecuta:
     ```sql
     SET LOCAL app.usuario = '<UUID_USUARIO>';
     SET LOCAL app.usuario_sig = '<FIRMA_HMAC>';
     ```
  4. Las políticas de PostgreSQL validan la firma HMAC con la función `gesta.fn_validar_firma_sesion()`. Si la firma es correcta, PostgreSQL restringe automáticamente las filas visibles únicamente al mercado o municipio asignado al operador.

### 3.4. Cobro en Campo Offline con Verificación Criptográfica
* Los inspectores en mercados sin cobertura celular generan cobros locales. El dispositivo firma el registro con un hash HMAC-SHA256 offline. Al sincronizar con la API, el procedimiento `fn_registrar_cobro_campo` valida la autenticidad del hash y la secuencia de folios antes de asentar el pago en el libro mayor contable.

---

## 4. Problemas Técnicos Superados Durante el Puesta a Punto

Para lograr que el proyecto compilara y arrancara al 100%, se diagnosticaron y solucionaron las siguientes incidencias:

1. **Conflicto de Scope en la Dependencia de PostgreSQL:**
   - *Problema:* El archivo `pom.xml` tenía configurado `<scope>runtime</scope>` para `org.postgresql:postgresql`. Esto impedía que el compilador `javac` encontrara las clases `PSQLException` y `ServerErrorMessage` referenciadas en el [GlobalExceptionHandler](file:///c:/Users/kaled/Downloads/Gestamercado/src/main/java/mx/gob/chiapas/gestamercado/api/common/exception/GlobalExceptionHandler.java).
   - *Solución:* Se ajustó el scope a compilación estándar en `pom.xml`.
2. **Desacoplamiento de Coordenadas en DTOs Geoespaciales:**
   - *Problema:* Los repositorios JDBC (`CobranzaJdbcRepository` y `TitularidadJdbcRepository`) invocaban métodos de conveniencia `.longitude()` y `.latitude()` sobre [GeoJsonPointDto](file:///c:/Users/kaled/Downloads/Gestamercado/src/main/java/mx/gob/chiapas/gestamercado/api/common/spatial/GeoJsonPointDto.java), pero el record solo exponía la lista genérica de coordenadas.
   - *Solución:* Se implementaron los métodos de extracción directa asegurando orden GeoJSON `[0]=Longitud, [1]=Latitud`.
3. **Depuración de Falsos Positivos en el Editor (VS Code):**
   - *Problema:* VS Code reportaba falsos errores de análisis de nulos por tener activa la directiva `"java.compile.nullAnalysis.mode": "automatic"`.
   - *Solución:* Se ajustó [.vscode/settings.json](file:///c:/Users/kaled/Downloads/Gestamercado/.vscode/settings.json) a `"disabled"`, permitiendo que el analizador se apegue a la especificación de `javac`.
4. **Restauración Automatizada del Esquema PostgreSQL 18:**
   - *Problema:* El volcado [GestaMercadoDB9_1.sql](file:///c:/Users/kaled/Downloads/Gestamercado/GestaMercadoDB9_1.sql) incluía meta-comandos de psql (`\restrict`, `\unrestrict`, bloques `COPY ... FROM stdin;`) que el Query Tool de pgAdmin rechazaba por no ser sentencias SQL estándar.
   - *Solución:* Se crearon previamente los roles de base de datos (`gesta_owner`, `gesta_app`, `gesta_batch`, `gesta_auth`) y se ejecutó la restauración oficial por canal de streaming nativo usando `psql.exe`.

---

## 5. Estado Actual: ¿Qué está listo?

* ✅ **Base de Datos:** PostgreSQL 18 activo con esquema `gesta` completamente migrado, triggers de cálculo, secuencias y extensiones `postgis`, `btree_gist`, `citext`, `pgcrypto`.
* ✅ **Compilación:** 250 clases Java compiladas con `mvn clean compile` sin un solo error o advertencia crítica (`BUILD SUCCESS`).
* ✅ **Servidor Web:** Tomcat levantado en el puerto `8080` en menos de 8 segundos con contexto `/`.
* ✅ **Documentación Activa:** Swagger UI operativo en `http://localhost:8080/swagger-ui.html`.
* ✅ **Monitoreo:** Endpoints de observabilidad de Spring Boot Actuator (`/actuator/health`) informando estado `UP`.

---

## 6. Roadmap: ¿Qué falta para considerar la API 100% terminada?

Para llevar esta API de su estado actual a un despliegue en producción gubernamental, los siguientes módulos deben implementarse:

| Fase | Módulo / Requerimiento | Descripción Técnica |
| :---: | :--- | :--- |
| **1** | **Batería de Pruebas Automatizadas** | Implementar pruebas unitarias en `src/test/java` con JUnit 5 y Mockito para los servicios, y pruebas de integración con `MockMvc` contra una base de datos PostgreSQL de pruebas. |
| **2** | **Generación de Reportes PDF/Excel** | Integración con JasperReports o Apache POI para emitir cédulas de concesión oficiales, recibos de pago con código QR y reportes de cortes de caja para entrega a tesorería. |
| **3** | **Rate Limiting y Protección Antifraude** | Configurar **Bucket4j** o Redis para aplicar limitación de tasa (*Rate Limiting*) en `/api/v1/auth/login`, previniendo ataques de fuerza bruta o saturación de peticiones. |
| **4** | **Caché Distribuido** | Implementar `@Cacheable` con Redis para catálogos de baja volatilidad (Municipios, Giros Comerciales, Valores UMA). |
| **5** | **CI/CD y Despliegue en Servidor** | Crear pipeline en GitHub Actions (`.github/workflows/deploy.yml`) para compilación automatizada y despliegue del paquete `.jar` ejecutable al servidor de producción. |

---

## 7. Preguntas Clave para Responder ante tu Profesor

1. **¿Por qué usar PostgreSQL y no MySQL?**  
   *Respuesta:* GestaMercado requiere **PostGIS** para operaciones geométricas nativas (SRID 4326) y **Row Level Security (RLS)** a nivel de motor de base de datos, además de tipos avanzados como `citext` para búsquedas case-insensitive sin costo de rendimiento. MySQL carece de la madurez de RLS y del ecosistema geoespacial de PostGIS.
2. **¿Por qué Argon2id en lugar de BCrypt?**  
   *Respuesta:* BCrypt es intensivo solo en uso de CPU, lo que lo hace vulnerable a ataques de fuerza bruta masiva usando tarjetas gráficas (GPUs) o granjas ASIC. Argon2id es un algoritmo *Memory-Hard*: exige consumir memoria RAM obligatoria (64 MB por hash), neutralizando ataques paralelos de hardware.
3. **¿Cómo garantizan que un locatario no acceda a datos de la administración fiscal?**  
   *Respuesta:* A través de una defensa en dos niveles: en la capa web mediante `@PreAuthorize` y roles JWT (`ADMINISTRADOR`, `INSPECTOR`, `LOCATARIO`), y en la capa de base de datos mediante **Row Level Security** alimentado con la firma criptográfica HMAC de la sesión.
