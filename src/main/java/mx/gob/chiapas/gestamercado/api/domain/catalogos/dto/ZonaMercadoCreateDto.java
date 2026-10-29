package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;

@Schema(description = "Solicitud de creación de zona interna de mercado")
public record ZonaMercadoCreateDto(
        @NotNull(message = "El identificador del mercado es obligatorio")
        Long mercadoId,

        @NotBlank(message = "El nombre de la zona es obligatorio")
        String nombre,

        GeoJsonPolygonDto geom
) {}
