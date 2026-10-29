package mx.gob.chiapas.gestamercado.api.common.spatial;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeoJsonMapper {

    private static final int SRID_WGS84 = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID_WGS84);

    public GeoJsonPointDto toPointDto(Point point) {
        if (point == null) {
            return null;
        }
        return new GeoJsonPointDto("Point", List.of(point.getX(), point.getY()));
    }

    public Point toPointEntity(GeoJsonPointDto dto) {
        if (dto == null || dto.coordinates() == null || dto.coordinates().size() < 2) {
            return null;
        }
        double x = dto.coordinates().get(0); // Longitud
        double y = dto.coordinates().get(1); // Latitud
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(SRID_WGS84);
        return point;
    }

    public GeoJsonPolygonDto toPolygonDto(Polygon polygon) {
        if (polygon == null) {
            return null;
        }
        List<List<List<Double>>> ringsList = new ArrayList<>();

        // Anillo exterior
        List<List<Double>> exteriorRing = new ArrayList<>();
        for (Coordinate coord : polygon.getExteriorRing().getCoordinates()) {
            exteriorRing.add(List.of(coord.getX(), coord.getY()));
        }
        ringsList.add(exteriorRing);

        // Anillos interiores (huecos si existen)
        int numHoles = polygon.getNumInteriorRing();
        for (int i = 0; i < numHoles; i++) {
            List<List<Double>> holeRing = new ArrayList<>();
            for (Coordinate coord : polygon.getInteriorRingN(i).getCoordinates()) {
                holeRing.add(List.of(coord.getX(), coord.getY()));
            }
            ringsList.add(holeRing);
        }

        return new GeoJsonPolygonDto("Polygon", ringsList);
    }

    public Polygon toPolygonEntity(GeoJsonPolygonDto dto) {
        if (dto == null || dto.coordinates() == null || dto.coordinates().isEmpty()) {
            return null;
        }
        List<List<List<Double>>> rings = dto.coordinates();

        // Anillo exterior
        List<List<Double>> exteriorCoords = rings.get(0);
        Coordinate[] extArray = toCoordinateArray(exteriorCoords);
        LinearRing exteriorLinearRing = geometryFactory.createLinearRing(extArray);

        // Anillos interiores (si hay)
        LinearRing[] interiorRings = null;
        if (rings.size() > 1) {
            interiorRings = new LinearRing[rings.size() - 1];
            for (int i = 1; i < rings.size(); i++) {
                Coordinate[] holeArray = toCoordinateArray(rings.get(i));
                interiorRings[i - 1] = geometryFactory.createLinearRing(holeArray);
            }
        }

        Polygon polygon = geometryFactory.createPolygon(exteriorLinearRing, interiorRings);
        polygon.setSRID(SRID_WGS84);
        return polygon;
    }

    private Coordinate[] toCoordinateArray(List<List<Double>> ringPoints) {
        Coordinate[] coords = new Coordinate[ringPoints.size()];
        for (int i = 0; i < ringPoints.size(); i++) {
            List<Double> pt = ringPoints.get(i);
            coords[i] = new Coordinate(pt.get(0), pt.get(1));
        }
        return coords;
    }
}
