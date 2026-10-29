package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DepositoHaciendaDto(
        UUID id,
        Long mercadoId,
        LocalDate fechaDeposito,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        BigDecimal montoDepositado,
        String referencia,
        UUID depositadoPor,
        String tarjetaFolio,
        BigDecimal tarjetaMonto,
        BigDecimal diferencia,
        Instant creadoEn,
        String justificacionDiferencia
) {}
