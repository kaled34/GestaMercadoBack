package mx.gob.chiapas.gestamercado.api.config;

import mx.gob.chiapas.gestamercado.api.security.RlsContextHolder;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

/**
 * Aspecto AOP transversal para inyección obligatoria del contexto RLS (Row Level Security)
 * en PostgreSQL al inicio de cada transacción demarcada con @Transactional.
 *
 * Inyecta:
 * 1. app.usuario_id: UUID del usuario autenticado o UUID de sistema.
 * 2. app.usuario_sig: Firma criptográfica HMAC-SHA256 generada sobre (app.usuario_id:txid_current())
 *    para satisfacer la verificación fail-closed de gesta.app_usuario() en el esquema gesta.
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // Se ejecuta dentro de la transacción activa iniciada por Spring
public class RlsTransactionAspect {

    private static final Logger log = LoggerFactory.getLogger(RlsTransactionAspect.class);

    private final JdbcClient jdbcClient;
    private final RlsProperties rlsProperties;

    public RlsTransactionAspect(JdbcClient jdbcClient, RlsProperties rlsProperties) {
        this.jdbcClient = jdbcClient;
        this.rlsProperties = rlsProperties;
    }

    @Before("@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@within(org.springframework.transaction.annotation.Transactional)")
    public void injectRlsSessionContext() {
        if (!rlsProperties.isEnabled()) {
            return;
        }

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.trace("No hay transacción SQL activa; se omite la inyección de app.usuario_id");
            return;
        }

        UUID effectiveUserId;
        if (RlsContextHolder.isSystemContext()) {
            effectiveUserId = (RlsContextHolder.getExplicitUserId() != null)
                    ? RlsContextHolder.getExplicitUserId()
                    : rlsProperties.getSystemUserId();
        } else if (RlsContextHolder.getExplicitUserId() != null) {
            effectiveUserId = RlsContextHolder.getExplicitUserId();
        } else {
            effectiveUserId = SecurityUtils.getCurrentUserId().orElse(null);
        }

        if (effectiveUserId == null) {
            log.trace("Transacción sin usuario autenticado previo (flujo público/auth); se difiere inyección RLS");
            return;
        }

        injectUserContext(effectiveUserId);
    }

    /**
     * Inyecta explícitamente el usuario y su firma criptográfica HMAC-SHA256 en la transacción SQL actual.
     */
    public void injectUserContext(UUID effectiveUserId) {
        if (!rlsProperties.isEnabled() || effectiveUserId == null) {
            return;
        }

        try {
            // Ejecución atómica de set_config para usuario_id y usuario_sig (HMAC sobre txid_current)
            jdbcClient.sql("""
                    SELECT set_config('app.usuario_id', :userId, true),
                           set_config('app.usuario_sig', encode(public.hmac(convert_to(:userId || ':' || txid_current()::text, 'UTF8'), decode(:hmacSecret, 'hex'), 'sha256'), 'hex'), true)
                    """)
                    .param("userId", effectiveUserId.toString())
                    .param("hmacSecret", rlsProperties.getHmacSecretHex())
                    .query()
                    .singleRow();

            log.trace("Contexto RLS inyectado exitosamente: app.usuario_id={}", effectiveUserId);
        } catch (Exception ex) {
            log.error("Fallo al inyectar contexto RLS para usuario {}: {}", effectiveUserId, ex.getMessage());
            throw ex;
        }
    }
}
