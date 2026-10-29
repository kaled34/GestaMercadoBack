package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EfectivoPendienteDepositoDto(
        Long mercadoId,
        UUID cobradorId,
        Long pagos,
        BigDecimal monto,
        Instant masAntiguo
) {}
