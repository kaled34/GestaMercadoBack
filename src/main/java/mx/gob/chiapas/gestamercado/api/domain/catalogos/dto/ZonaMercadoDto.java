package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;

@Schema(description = "Detalle de una zona interna de mercado")
public record ZonaMercadoDto(
        Long id,
        Long mercadoId,
        String nombre,
        GeoJsonPolygonDto geom
) {}
