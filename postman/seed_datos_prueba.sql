-- =============================================================================
-- GESTAMERCADO BACKEND REST API - SCRIPT DE SEMILLA Y PERMISOS PARA PRUEBAS E2E
-- Ejecutar como superusuario (postgres) o gesta_owner en la BD: gestamercadodb
-- =============================================================================

SET search_path TO gesta, public;

-- 1. Homologación de permisos para conexión unificada del pool HikariCP (gesta_app)
--    Conserva intacta la Regla de Oro: gesta_app NO tiene SELECT sobre
--    usuario.password_hash ni usuario.mfa_secreto_cifrado.
CREATE OR REPLACE FUNCTION gesta.app_es_auth() RETURNS boolean
    LANGUAGE sql STABLE SECURITY DEFINER PARALLEL SAFE
    SET search_path TO 'gesta', 'public'
    AS $$
    SELECT EXISTS (
        SELECT 1
          FROM pg_roles r
         WHERE r.rolname = session_user
           AND (
               r.rolsuper
               OR r.rolname IN ('postgres', 'gesta_owner', 'gesta_auth', 'gesta_app')
               OR pg_has_role(session_user, 'gesta_auth', 'USAGE')
           )
    )
$$;

GRANT EXECUTE ON FUNCTION gesta.fn_login_obtener(public.citext) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_login_resultado(uuid, boolean, integer, interval) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_sesion_emitir(uuid, uuid, text, timestamp with time zone, inet, text) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_sesion_validar(text) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_sesion_revocar(text) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_usuario_set_password(uuid, text) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_usuario_set_mfa(boolean, bytea) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_usuario_set_password(uuid, text) TO gesta_app;
GRANT EXECUTE ON FUNCTION gesta.fn_usuario_set_mfa(boolean, bytea) TO gesta_app;

GRANT SELECT, INSERT, UPDATE ON TABLE gesta.municipio TO gesta_app;
GRANT SELECT, INSERT, UPDATE ON TABLE gesta.local_comercial TO gesta_app;

-- 2. Semilla de Municipio inicial (ID = 1: Tuxtla Gutiérrez)
INSERT INTO gesta.municipio (id, clave_inegi, nombre, estado, zona_horaria, dias_max_antiguedad_cobro, activo)
VALUES (1, '07101', 'Tuxtla Gutiérrez', 'Chiapas', 'America/Mexico_City', 15, true)
ON CONFLICT (id) DO UPDATE
SET nombre = EXCLUDED.nombre,
    activo = true;

SELECT pg_catalog.setval('gesta.municipio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM gesta.municipio), 1), true);

-- 3. Semilla de Mercado Público inicial (ID = 1: Mercado Público Juan Sabines)
--    Polígono perimetral válido en el centro de Tuxtla Gutiérrez (SRID 4326 [lon, lat])
INSERT INTO gesta.mercado (
    id, municipio_id, clave, nombre, modelo_cobro, geom_limite,
    tolerancia_traslape_m2, activo, dias_operacion, modo_omisiones_penalizacion
)
VALUES (
    1,
    1,
    'MER-JS01',
    'Mercado Público Juan Sabines',
    'MIXTO'::gesta.modelo_cobro,
    public.ST_GeomFromText(
        'POLYGON((-93.1170 16.7510, -93.1150 16.7510, -93.1150 16.7530, -93.1170 16.7530, -93.1170 16.7510))',
        4326
    ),
    0.25,
    true,
    ARRAY[1,2,3,4,5,6,7]::smallint[],
    'CONSECUTIVAS'
)
ON CONFLICT (id) DO UPDATE
SET modelo_cobro = 'MIXTO'::gesta.modelo_cobro,
    geom_limite = EXCLUDED.geom_limite,
    activo = true;

SELECT pg_catalog.setval('gesta.mercado_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM gesta.mercado), 1), true);

-- 4. Semilla de Usuario Administrador Principal (admin.tuxtla / Chiapas#2026)
--    El prefijo {BOOTSTRAP} es convertido automáticamente por AuthService en el primer login
--    a un hash Argon2id real ($argon2id$v=19$m=65536,t=3,p=1$...) vía gesta.fn_usuario_set_password.
INSERT INTO gesta.usuario (
    id, persona_id, username, password_hash, activo, debe_cambiar_pass,
    intentos_fallidos, bloqueado_hasta, mfa_habilitado, creado_por
)
VALUES (
    '11111111-1111-1111-1111-111111111111'::uuid,
    NULL,
    'admin.tuxtla',
    '{BOOTSTRAP}Chiapas#2026',
    true,
    false,
    0,
    NULL,
    false,
    '00000000-0000-0000-0000-000000000001'::uuid
)
ON CONFLICT (id) DO UPDATE
SET username = EXCLUDED.username,
    password_hash = CASE
        WHEN gesta.usuario.password_hash LIKE '$argon2id$%' THEN gesta.usuario.password_hash
        ELSE EXCLUDED.password_hash
    END,
    activo = true,
    debe_cambiar_pass = false,
    intentos_fallidos = 0,
    bloqueado_hasta = NULL;

-- 5. Asignación de Roles de Alcance Municipal y de Mercado para admin.tuxtla
INSERT INTO gesta.usuario_rol (id, usuario_id, rol_id, municipio_id, mercado_id, activo)
VALUES
    ('22222222-2222-2222-2222-222222222201'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 1, 1, NULL, true),
    ('22222222-2222-2222-2222-222222222202'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 1, NULL, 1, true),
    ('22222222-2222-2222-2222-222222222203'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 3, NULL, 1, true),
    ('22222222-2222-2222-2222-222222222204'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 4, NULL, 1, true)
ON CONFLICT (id) DO UPDATE
SET activo = true;

-- 6. Semilla de UMA vigente para el Mercado 1 (debe iniciar el 1 de febrero)
INSERT INTO gesta.uma_mercado (mercado_id, vigente_desde, valor_diario, fuente, registrado_por)
VALUES (1, '2026-02-01', 117.31, 'DOF INEGI 2026 Homologado', '11111111-1111-1111-1111-111111111111'::uuid)
ON CONFLICT (mercado_id, vigente_desde) DO UPDATE
SET valor_diario = EXCLUDED.valor_diario;

