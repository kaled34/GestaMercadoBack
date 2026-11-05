@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ============================================================================
echo   GESTAMERCADO BACKEND REST API - EJECUTOR AUTOMATIZADO NEWMAN E2E
echo ============================================================================
echo.

REM Asegurar que PowerShell y System32 esten en el PATH de la sesion actual
set "PATH=%PATH%;C:\Windows\System32;C:\Windows\System32\WindowsPowerShell\v1.0;C:\Program Files\PostgreSQL\18\bin;C:\Program Files\nodejs"

cd /d "%~dp0\.."

echo [1/3] Aplicando semilla SQL y permisos de autenticacion en PostgreSQL (gestamercadodb)...
where psql >nul 2>nul
if errorlevel 1 (
    echo [AVISO] No se encontro psql.exe en el PATH. Asegurate de haber ejecutado postman\seed_datos_prueba.sql en pgAdmin o psql.
) else (
    psql -U postgres -h localhost -p 5432 -d gestamercadodb -f "postman\seed_datos_prueba.sql"
    if errorlevel 1 (
        echo [ERROR] Fallo la ejecucion de postman\seed_datos_prueba.sql en PostgreSQL.
        exit /b 1
    )
    echo       [OK] Semilla de datos y permisos PL/pgSQL aplicados correctamente.
)
echo.

echo [2/3] Verificando disponibilidad de la API en http://localhost:8080/actuator/health ...
curl -s http://localhost:8080/actuator/health | findstr "UP" >nul
if errorlevel 1 (
    echo [ERROR] La API no responde en http://localhost:8080.
    echo         Asegurate de compilar y reiniciar Spring Boot con:
    echo         mvn clean spring-boot:run
    exit /b 1
)
echo       [OK] API GestaMercado en linea ^(status: UP^).
echo.

echo [3/3] Ejecutando suite de pruebas E2E con Newman CLI...
echo.
call npx --yes newman run "postman\GestaMercado_Postman_Collection.json" -e "postman\GestaMercado_Postman_Environment.json" --color on --delay-request 100

if errorlevel 1 (
    echo.
    echo [FALLO] Algunas aserciones de Newman reportaron error. Revisa el detalle superior.
    exit /b 1
) else (
    echo.
    echo [EXITO] Todas las pruebas End-to-End de GestaMercado pasaron correctamente.
)

endlocal

