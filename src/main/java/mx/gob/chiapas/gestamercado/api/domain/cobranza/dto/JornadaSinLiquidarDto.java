package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JornadaSinLiquidarDto(
        LocalDate fechaLocal,
        Long pagos,
        BigDecimal monto
) {}
