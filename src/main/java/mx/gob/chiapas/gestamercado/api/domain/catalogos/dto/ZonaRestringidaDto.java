package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoZonaRest;

@Schema(description = "Detalle de una zona restringida o protegida municipal")
public record ZonaRestringidaDto(
        Long id,
        Long municipioId,
        TipoZonaRest tipo,
        String nombre,
        GeoJsonPolygonDto geom
) {}
