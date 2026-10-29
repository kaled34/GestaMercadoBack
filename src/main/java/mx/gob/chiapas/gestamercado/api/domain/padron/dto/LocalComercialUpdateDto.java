package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;

import java.math.BigDecimal;

@Schema(description = "Solicitud de actualización de local comercial")
public record LocalComercialUpdateDto(
        Long zonaId,
        String nombreComercial,
        String folioTarjeton,
        TipoEspacio tipoEspacio,
        EstatusLocal estatus,
        Long giroObservadoId,
        GeoJsonPolygonDto geom,
        BigDecimal areaOficialM2,
        String areaOficialMotivo,
        BigDecimal metrosLineales,
        String notas
) {}
