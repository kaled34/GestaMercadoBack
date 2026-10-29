-- Creación de roles previos requeridos por el dump GestaMercado
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_owner') THEN
        CREATE ROLE gesta_owner WITH LOGIN PASSWORD 'gesta_owner_password' SUPERUSER;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_app') THEN
        CREATE ROLE gesta_app WITH LOGIN PASSWORD 'gesta_app_password';
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_batch') THEN
        CREATE ROLE gesta_batch WITH LOGIN PASSWORD 'gesta_batch_password';
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'gesta_auth') THEN
        CREATE ROLE gesta_auth WITH LOGIN PASSWORD 'gesta_auth_password';
    END IF;
END $$;
