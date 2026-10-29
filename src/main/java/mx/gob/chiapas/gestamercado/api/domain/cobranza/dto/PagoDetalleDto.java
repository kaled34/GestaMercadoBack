package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PagoDetalleDto(
        UUID pagoId,
        UUID cargoId,
        BigDecimal montoAplicado
) {}
