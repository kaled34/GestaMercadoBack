package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.domain.enums.CanalCobro;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PagoCajaDto(
        @NotNull(message = "El ID del local comercial es obligatorio")
        UUID localId,

        @NotBlank(message = "El folio de cobro en ventanilla es obligatorio")
        String folio,

        @NotNull(message = "El monto cobrado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto cobrado debe ser mayor a 0")
        BigDecimal monto,

        Instant fecha,
        FormaPago formaPago,
        CanalCobro canal,
        UUID loteId,
        String hashFila
) {}
