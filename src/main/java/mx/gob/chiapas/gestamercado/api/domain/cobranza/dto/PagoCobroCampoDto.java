package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PagoCobroCampoDto(
        @NotNull(message = "El ID del pago generado por el dispositivo es obligatorio")
        UUID pagoId,

        @NotNull(message = "El ID del local comercial es obligatorio")
        UUID localId,

        @NotNull(message = "El ID del cobrador es obligatorio")
        UUID cobradorId,

        @NotNull(message = "El ID del dispositivo es obligatorio")
        UUID dispositivoId,

        @NotBlank(message = "El folio de cobro es obligatorio")
        String folio,

        @NotNull(message = "El monto cobrado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto cobrado debe ser mayor a 0")
        BigDecimal montoCobrado,

        @NotNull(message = "La fecha y hora de emisión en el dispositivo es obligatoria")
        Instant fecha,

        @NotBlank(message = "El hash offline HMAC-SHA256 es obligatorio")
        String hashOffline,

        BigDecimal unidades,
        FormaPago formaPago,
        GeoJsonPointDto ubicacion,
        LocalDate fechaInicioCargo
) {}
