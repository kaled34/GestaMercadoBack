package mx.gob.chiapas.gestamercado.api.domain.fiscal.service;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.repository.FiscalJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Tarea programada en segundo plano para la ejecución diaria del motor fiscal de recargos moratorios.
 * El contexto RLS inyecta de forma transparente el UUID del sistema (00000000-0000-0000-0000-000000000001)
 * con su respectiva firma criptográfica HMAC-SHA256 en cada transacción.
 */
@Component
public class FiscalScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(FiscalScheduledTask.class);

    private final MercadoJpaRepository mercadoRepository;
    private final FiscalJdbcRepository fiscalJdbcRepository;

    public FiscalScheduledTask(MercadoJpaRepository mercadoRepository,
                               FiscalJdbcRepository fiscalJdbcRepository) {
        this.mercadoRepository = mercadoRepository;
        this.fiscalJdbcRepository = fiscalJdbcRepository;
    }

    @Scheduled(cron = "${gesta.fiscal.recargos-cron:0 0 2 * * ?}")
    @Transactional
    public void ejecutarMotorRecargosDiarios() {
        log.info("Iniciando corrida programada del motor fiscal de recargos moratorios...");
        List<MercadoEntity> mercadosActivos = mercadoRepository.findByActivoTrue();
        LocalDate hoy = LocalDate.now();

        int mercadosProcesados = 0;
        int recargosTotales = 0;

        for (MercadoEntity mercado : mercadosActivos) {
            try {
                Integer recargos = fiscalJdbcRepository.generarRecargos(mercado.getId(), hoy);
                recargosTotales += (recargos != null ? recargos : 0);
                mercadosProcesados++;
                log.debug("Mercado {} ({}): {} recargos generados", mercado.getId(), mercado.getNombre(), recargos);
            } catch (Exception e) {
                log.error("Error al procesar recargos automáticos para mercado {} ({}): {}",
                        mercado.getId(), mercado.getNombre(), e.getMessage(), e);
            }
        }

        log.info("Corrida de recargos finalizada: {} mercados procesados, {} recargos moratorios emitidos.",
                mercadosProcesados, recargosTotales);
    }
}
