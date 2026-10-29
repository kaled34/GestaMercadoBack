package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SaldoFavorDto(
        UUID localId,
        BigDecimal saldoFavor,
        UUID titularPersonaId
) {}
