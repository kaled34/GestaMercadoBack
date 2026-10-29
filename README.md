# GestaMercado - Backend API RESTful

Backend gubernamental para la plataforma **GestaMercado** (Gobierno de Chiapas) desarrollado con **Spring Boot 3.3.5**, **Java 21**, **PostgreSQL con PostGIS**, **Row Level Security (RLS)** y **Argon2id**.

---

## 📋 Requisitos Previos

1. **Java JDK 21** instalado y configurado en las variables de entorno (`PATH`).
2. **Maven 3.9+** (configurado en el sistema).
3. **PostgreSQL 18** (o 16+) instalado localmente con la extensión **PostGIS** habilitada.
4. **pgAdmin 4** para administración visual de base de datos.

---

## 🚀 Guía de Inicio y Configuración de Base de Datos

### Paso 1: Configurar Roles y Base de Datos (en pgAdmin o psql)

Abre **pgAdmin 4** con tu usuario `postgres` y ejecuta en el Query Tool:

```sql
-- 1. Crear o actualizar los roles del sistema
ALTER ROLE gesta_owner WITH LOGIN PASSWORD 'gesta_owner_password' SUPERUSER;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_app') THEN
        CREATE ROLE gesta_app WITH LOGIN PASSWORD 'gesta_app_password';
    ELSE
        ALTER ROLE gesta_app WITH LOGIN PASSWORD 'gesta_app_password';
    END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_batch') THEN
        CREATE ROLE gesta_batch WITH LOGIN PASSWORD 'gesta_batch_password';
    ELSE
        ALTER ROLE gesta_batch WITH LOGIN PASSWORD 'gesta_batch_password';
    END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_auth') THEN
        CREATE ROLE gesta_auth WITH LOGIN PASSWORD 'gesta_auth_password';
    ELSE
        ALTER ROLE gesta_auth WITH LOGIN PASSWORD 'gesta_auth_password';
    END IF;
END $$;

-- 2. Crear la base de datos con soporte UTF-8
CREATE DATABASE gestamercadodb OWNER gesta_owner ENCODING 'UTF8';
```

---

### Paso 2: Restaurar el Esquema y Datos Oficiales

Desde tu terminal de PowerShell en la raíz del proyecto, ejecuta el cliente nativo `psql`:

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d gestamercadodb -f GestaMercadoDB9_1.sql
```
*(Ingresa tu contraseña de `postgres` cuando sea solicitada).*

---

### Paso 3: Compilar y Ejecutar el Backend

Una vez restaurada la base de datos:

```powershell
mvn clean compile
mvn spring-boot:run
```

La aplicación arrancará en el puerto **`8080`**.

---

## 📖 Documentación Interactiva (Swagger UI)

Una vez iniciada la aplicación, accede desde tu navegador a:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 🔐 Módulos Principales de la API

| Módulo | Endpoint Base | Descripción |
| :--- | :--- | :--- |
| **Autenticación** | `/api/v1/auth` | Login con Argon2id, renovación de JWT y control de sesiones |
| **Usuarios** | `/api/v1/usuarios` | Gestión de usuarios y asignación de roles |
| **Dispositivos** | `/api/v1/dispositivos` | Control y autorización de dispositivos móviles |
| **Catálogos** | `/api/v1/catalogos` | Mercados, Municipios, Giros, Zonas y UMA |
| **Padrón** | `/api/v1/padron` | Locales comerciales, polígonos y asistencias |
| **Titularidad** | `/api/v1/titularidad` | Concesiones, traspasos y personas físicas/morales |
| **Fiscal** | `/api/v1/fiscal` | Tarifas, reglas de mora y cálculo de recargos |
| **Cobranza** | `/api/v1/cobranza` | Pagos, cortes de caja y depósitos a Hacienda |
| **Reportes** | `/api/v1/reportes` | Reportes de recaudación y auditoría |
