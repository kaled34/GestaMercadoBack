package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PagoSinCorteDto(
        Long mercadoId,
        UUID cobradorId,
        LocalDate fechaLocal,
        Long pagos,
        BigDecimal monto
) {}
