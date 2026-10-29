package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPolygonDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.ModeloCobro;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Solicitud de alta de mercado público")
public record MercadoCreateDto(
        @Schema(description = "Identificador del municipio")
        @NotNull(message = "El municipio es obligatorio")
        Long municipioId,

        @Schema(description = "Clave interna única", example = "MER-01")
        @NotBlank(message = "La clave del mercado es obligatoria")
        String clave,

        @Schema(description = "Nombre oficial del mercado", example = "Mercado Público Dr. Rafael Pascacio Gamboa")
        @NotBlank(message = "El nombre del mercado es obligatorio")
        String nombre,

        @Schema(description = "Modelo de cobro fiscal")
        @NotNull(message = "El modelo de cobro es obligatorio")
        ModeloCobro modeloCobro,

        @Schema(description = "Polígono perimetral del mercado")
        GeoJsonPolygonDto geomLimite,

        @Schema(description = "Tolerancia de traslape espacial en m2", example = "0.25")
        BigDecimal toleranciaTraslapeM2,

        @Schema(description = "Días de operación (1=Lunes ... 7=Domingo)", example = "[1, 2, 3, 4, 5, 6, 7]")
        List<Short> diasOperacion,

        @Schema(description = "Modo de penalización de omisiones: CONSECUTIVAS o ACUMULADAS", example = "CONSECUTIVAS")
        String modoOmisionesPenalizacion
) {}
