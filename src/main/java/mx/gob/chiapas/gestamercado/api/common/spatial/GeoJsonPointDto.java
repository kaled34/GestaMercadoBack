package mx.gob.chiapas.gestamercado.api.common.spatial;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representación estándar GeoJSON para un punto geográfico (SRID 4326)")
public record GeoJsonPointDto(
        @Schema(description = "Tipo de geometría", example = "Point")
        String type,

        @Schema(description = "Coordenadas en formato [longitud, latitud]", example = "[-93.116667, 16.75]")
        List<Double> coordinates
) {
    public GeoJsonPointDto {
        if (type == null) {
            type = "Point";
        }
    }

    public static GeoJsonPointDto of(double longitude, double latitude) {
        return new GeoJsonPointDto("Point", List.of(longitude, latitude));
    }

    public Double longitude() {
        return (coordinates != null && !coordinates.isEmpty()) ? coordinates.get(0) : null;
    }

    public Double latitude() {
        return (coordinates != null && coordinates.size() > 1) ? coordinates.get(1) : null;
    }
}
