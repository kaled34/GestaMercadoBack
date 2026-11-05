package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de bajo nivel que interactúa con las funciones criptográficas
 * y de sesión del esquema gesta usando JdbcClient.
 *
 * CUMPLE LA REGLA INQUEBRANTABLE:
 * No hace SELECT directo sobre password_hash ni mfa_secreto_cifrado.
 * Toda validación, bloqueo de fuerza bruta y emisión de sesión refresh
 * se ejecuta a través de funciones PL/pgSQL SECURITY DEFINER.
 */
@Repository
public class AuthJdbcRepository {

    private static final Logger log = LoggerFactory.getLogger(AuthJdbcRepository.class);

    private final JdbcClient jdbcClient;

    public AuthJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public record LoginUsuarioData(
            UUID id,
            String passwordHash,
            boolean activo,
            boolean debeCambiarPass,
            short intentosFallidos,
            Instant bloqueadoHasta,
            boolean mfaHabilitado,
            byte[] mfaSecretoCifrado
    ) {}

    public record SesionValidadaData(
            UUID sesionId,
            UUID usuarioId,
            UUID dispositivoId,
            Instant expiraEn
    ) {}

    /**
     * Invoca gesta.fn_login_obtener(CAST(:username AS public.citext)).
     */
    public Optional<LoginUsuarioData> loginObtener(String username) {
        return jdbcClient.sql("""
                SELECT id,
                       password_hash,
                       activo,
                       debe_cambiar_pass,
                       intentos_fallidos,
                       bloqueado_hasta,
                       mfa_habilitado,
                       mfa_secreto_cifrado
                  FROM gesta.fn_login_obtener(CAST(:username AS public.citext))
                """)
                .param("username", username)
                .query((rs, rowNum) -> {
                    UUID id = rs.getObject("id", UUID.class);
                    String passHash = rs.getString("password_hash");
                    boolean activo = rs.getBoolean("activo");
                    boolean debeCambiar = rs.getBoolean("debe_cambiar_pass");
                    short intentos = rs.getShort("intentos_fallidos");
                    Timestamp tsBloqueo = rs.getTimestamp("bloqueado_hasta");
                    Instant bloqueadoHasta = (tsBloqueo != null) ? tsBloqueo.toInstant() : null;
                    boolean mfaHabilitado = rs.getBoolean("mfa_habilitado");
                    byte[] mfaSecreto = rs.getBytes("mfa_secreto_cifrado");

                    return new LoginUsuarioData(id, passHash, activo, debeCambiar, intentos, bloqueadoHasta, mfaHabilitado, mfaSecreto);
                })
                .optional();
    }

    /**
     * Registra el resultado del intento de autenticación (éxito o fallo con incremento de contador)
     * e implementa el bloqueo temporal contra ataques de fuerza bruta.
     */
    public void loginResultado(UUID usuarioId, boolean exito, int maxIntentos, String intervaloBloqueo) {
        jdbcClient.sql("""
                SELECT gesta.fn_login_resultado(
                    CAST(:usuarioId AS uuid),
                    :exito,
                    CAST(:maxIntentos AS integer),
                    CAST(:bloqueo AS interval)
                )
                """)
                .param("usuarioId", usuarioId)
                .param("exito", exito)
                .param("maxIntentos", maxIntentos)
                .param("bloqueo", intervaloBloqueo)
                .query()
                .singleRow();
    }

    /**
     * Emite una nueva sesión refresh persistida en gesta.sesion_refresh.
     */
    public UUID sesionEmitir(UUID usuarioId, UUID dispositivoId, String tokenHash, Instant expiraEn, String ip, String userAgent) {
        String clientIp = (ip != null && !ip.isBlank()) ? ip : "127.0.0.1";
        return jdbcClient.sql("""
                SELECT gesta.fn_sesion_emitir(
                    CAST(:usuarioId AS uuid),
                    CAST(:dispositivoId AS uuid),
                    CAST(:tokenHash AS text),
                    CAST(:expiraEn AS timestamptz),
                    CAST(:ip AS inet),
                    CAST(:userAgent AS text)
                )
                """)
                .param("usuarioId", usuarioId)
                .param("dispositivoId", dispositivoId)
                .param("tokenHash", tokenHash)
                .param("expiraEn", Timestamp.from(expiraEn))
                .param("ip", clientIp)
                .param("userAgent", userAgent)
                .query(UUID.class)
                .single();
    }

    /**
     * Valida la validez de un refresh token hash en gesta.sesion_refresh.
     */
    public Optional<SesionValidadaData> sesionValidar(String tokenHash) {
        return jdbcClient.sql("""
                SELECT sesion_id,
                       usuario_id,
                       dispositivo_id,
                       expira_en
                  FROM gesta.fn_sesion_validar(CAST(:tokenHash AS text))
                """)
                .param("tokenHash", tokenHash)
                .query((rs, rowNum) -> new SesionValidadaData(
                        rs.getObject("sesion_id", UUID.class),
                        rs.getObject("usuario_id", UUID.class),
                        rs.getObject("dispositivo_id", UUID.class),
                        rs.getTimestamp("expira_en").toInstant()
                ))
                .optional();
    }

    /**
     * Revoca un refresh token para cierre de sesión o rotación.
     */
    public boolean sesionRevocar(String tokenHash) {
        return Boolean.TRUE.equals(jdbcClient.sql("""
                SELECT gesta.fn_sesion_revocar(CAST(:tokenHash AS text))
                """)
                .param("tokenHash", tokenHash)
                .query(Boolean.class)
                .single());
    }

    /**
     * Actualiza la contraseña del usuario invocando gesta.fn_usuario_set_password.
     */
    public void usuarioSetPassword(UUID usuarioId, String passwordHash) {
        jdbcClient.sql("""
                SELECT gesta.fn_usuario_set_password(CAST(:usuarioId AS uuid), CAST(:passwordHash AS text))
                """)
                .param("usuarioId", usuarioId)
                .param("passwordHash", passwordHash)
                .query()
                .singleRow();
    }

    /**
     * Configura o deshabilita MFA para el usuario actual autenticado.
     */
    public void usuarioSetMfa(boolean habilitado, byte[] secretoCifrado) {
        jdbcClient.sql("""
                SELECT gesta.fn_usuario_set_mfa(:habilitado, CAST(:secretoCifrado AS bytea))
                """)
                .param("habilitado", habilitado)
                .param("secretoCifrado", secretoCifrado)
                .query()
                .singleRow();
    }

    /**
     * Provisiona o rota el secreto HMAC-SHA256 de un dispositivo móvil invocando
     * gesta.fn_dispositivo_rotar_secreto_hmac(p_dispositivo, p_secreto_hex).
     */
    public String dispositivoRotarSecretoHmac(UUID dispositivoId, String secretoHex) {
        return jdbcClient.sql("""
                SELECT gesta.fn_dispositivo_rotar_secreto_hmac(CAST(:dispositivoId AS uuid), CAST(:secretoHex AS text))
                """)
                .param("dispositivoId", dispositivoId)
                .param("secretoHex", secretoHex)
                .query(String.class)
                .single();
    }

    /**
     * Activa o desactiva un dispositivo móvil mediante gesta.fn_dispositivo_set_activo.
     */
    public void dispositivoSetActivo(UUID dispositivoId, boolean activo, String motivo) {
        jdbcClient.sql("""
                SELECT gesta.fn_dispositivo_set_activo(CAST(:dispositivoId AS uuid), :activo, CAST(:motivo AS text))
                """)
                .param("dispositivoId", dispositivoId)
                .param("activo", activo)
                .param("motivo", motivo)
                .query()
                .singleRow();
    }
}
