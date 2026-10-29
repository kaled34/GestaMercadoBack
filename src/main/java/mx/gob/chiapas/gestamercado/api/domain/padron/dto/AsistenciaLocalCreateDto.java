package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Solicitud de registro de asistencia o incidencia en local comercial")
public record AsistenciaLocalCreateDto(
        @NotNull(message = "El identificador del local es obligatorio")
        UUID localId,

        @NotNull(message = "La fecha de inspección es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "El tipo de incidencia es obligatorio")
        @Pattern(regexp = "^(PRESENTE|AUSENTE_CERRADO|NEGATIVA_PAGO)$", message = "Tipo de incidencia no válido (PRESENTE, AUSENTE_CERRADO, NEGATIVA_PAGO)")
        String tipoIncidencia,

        String observaciones,

        GeoJsonPointDto ubicacion
) {}
