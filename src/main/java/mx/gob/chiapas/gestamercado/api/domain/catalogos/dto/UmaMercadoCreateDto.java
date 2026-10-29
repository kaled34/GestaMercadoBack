package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Solicitud de captura o actualización de valor UMA por mercado")
public record UmaMercadoCreateDto(
        @NotNull(message = "El identificador del mercado es obligatorio")
        Long mercadoId,

        @Schema(description = "Fecha de inicio de vigencia (debe ser 1 de febrero conforme a la ley)", example = "2026-02-01")
        @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
        LocalDate vigenteDesde,

        @Schema(description = "Valor diario en pesos de la UMA", example = "113.14")
        @NotNull(message = "El valor diario es obligatorio")
        @Positive(message = "El valor diario debe ser positivo")
        BigDecimal valorDiario,

        @Schema(description = "Fuente o acuerdo de cabildo/INEGI")
        String fuente
) {}
