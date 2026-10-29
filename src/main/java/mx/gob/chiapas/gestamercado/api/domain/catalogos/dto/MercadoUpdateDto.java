package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.ModeloCobro;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Solicitud de modificación de mercado público")
public record MercadoUpdateDto(
        String nombre,
        ModeloCobro modeloCobro,
        GeoJsonPolygonDto geomLimite,
        BigDecimal toleranciaTraslapeM2,
        Boolean activo,
        List<Short> diasOperacion,
        String modoOmisionesPenalizacion
) {}
