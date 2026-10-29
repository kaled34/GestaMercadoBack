package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detalle del local comercial georreferenciado")
public record LocalComercialDto(
        UUID id,
        Long mercadoId,
        Long zonaId,
        String numeroLocal,
        String nombreComercial,
        String folioTarjeton,
        TipoEspacio tipoEspacio,
        EstatusLocal estatus,
        Long giroObservadoId,
        GeoJsonPolygonDto geom,
        BigDecimal areaCalculadaM2,
        BigDecimal perimetroM,
        GeoJsonPointDto centroide,
        BigDecimal areaOficialM2,
        String areaOficialMotivo,
        BigDecimal metrosLineales,
        String notas,
        Instant creadoEn,
        Instant actualizadoEn
) {}
