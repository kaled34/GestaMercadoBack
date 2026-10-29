package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Detalle de registro de asistencia o inspección de local")
public record AsistenciaLocalDto(
        UUID id,
        UUID localId,
        LocalDate fecha,
        UUID registradoPor,
        String observaciones,
        Instant creadoEn,
        String tipoIncidencia,
        GeoJsonPointDto ubicacion
) {}
