package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CorteCajaDto(
        UUID id,
        UUID cobradorId,
        Long mercadoId,
        LocalDate fechaCorte,
        Short secuencia,
        BigDecimal montoTotal,
        Integer numPagos,
        Instant liquidadoEn,
        BigDecimal montoDeclarado,
        BigDecimal diferencia,
        UUID recibidoPor,
        String observaciones
) {}
