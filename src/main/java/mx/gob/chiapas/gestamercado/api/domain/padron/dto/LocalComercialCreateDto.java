package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;

import java.math.BigDecimal;

@Schema(description = "Solicitud de alta de local comercial")
public record LocalComercialCreateDto(
        @NotNull(message = "El identificador del mercado es obligatorio")
        Long mercadoId,

        Long zonaId,

        @NotBlank(message = "El número o identificador del local es obligatorio")
        String numeroLocal,

        String nombreComercial,

        String folioTarjeton,

        @NotNull(message = "El tipo de espacio es obligatorio")
        TipoEspacio tipoEspacio,

        EstatusLocal estatus,

        Long giroObservadoId,

        GeoJsonPolygonDto geom,

        BigDecimal areaOficialM2,

        String areaOficialMotivo,

        BigDecimal metrosLineales,

        String notas
) {}
