package mx.gob.chiapas.gestamercado.api.common.spatial;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representación estándar GeoJSON para un polígono geográfico cerrado (SRID 4326)")
public record GeoJsonPolygonDto(
        @Schema(description = "Tipo de geometría", example = "Polygon")
        String type,

        @Schema(description = "Anillos de coordenadas del polígono: exterior y opcionales interiores [[[lon, lat], ...]]")
        List<List<List<Double>>> coordinates
) {
    public GeoJsonPolygonDto {
        if (type == null) {
            type = "Polygon";
        }
    }
}
