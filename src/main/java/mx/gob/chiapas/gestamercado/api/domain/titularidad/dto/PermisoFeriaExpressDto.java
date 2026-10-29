package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PermisoFeriaExpressDto(
        @NotNull(message = "El ID del local/espacio comercial es obligatorio")
        UUID localId,

        @NotNull(message = "El ID del titular es obligatorio")
        UUID titularPersonaId,

        @NotNull(message = "El ID del giro comercial es obligatorio")
        Long giroId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La duración en días es obligatoria")
        @Min(value = 1, message = "La duración mínima es de 1 día")
        @Max(value = 30, message = "La duración máxima de feria express es de 30 días")
        Short dias,

        @NotNull(message = "El ID del pago es obligatorio")
        UUID pagoId,

        @NotNull(message = "El ID del dispositivo es obligatorio")
        UUID dispositivoId,

        @NotBlank(message = "El folio de cobro es obligatorio")
        String folio,

        @NotNull(message = "El monto cobrado es obligatorio")
        BigDecimal montoCobrado,

        @NotBlank(message = "El hash criptográfico offline es obligatorio")
        String hashOffline,

        FormaPago formaPago,
        GeoJsonPointDto ubicacion,
        Instant fechaCobro
) {}
