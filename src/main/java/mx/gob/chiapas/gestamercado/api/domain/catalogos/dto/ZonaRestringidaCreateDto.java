package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoZonaRest;

@Schema(description = "Solicitud de alta de zona protegida o restringida")
public record ZonaRestringidaCreateDto(
        @NotNull(message = "El identificador del municipio es obligatorio")
        Long municipioId,

        @NotNull(message = "El tipo de zona restringida es obligatorio")
        TipoZonaRest tipo,

        String nombre,

        @NotNull(message = "El polígono georreferenciado es obligatorio")
        GeoJsonPolygonDto geom
) {}
