package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.ModeloCobro;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Detalle del mercado público")
public record MercadoDto(
        Long id,
        Long municipioId,
        String clave,
        String nombre,
        ModeloCobro modeloCobro,
        GeoJsonPolygonDto geomLimite,
        BigDecimal toleranciaTraslapeM2,
        boolean activo,
        Instant creadoEn,
        List<Short> diasOperacion,
        String modoOmisionesPenalizacion
) {}
