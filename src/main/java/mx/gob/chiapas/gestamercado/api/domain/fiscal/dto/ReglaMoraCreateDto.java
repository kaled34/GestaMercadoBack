package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReglaMoraCreateDto(
        @NotNull(message = "El municipio es obligatorio")
        Long municipioId,

        Long mercadoId,

        @NotNull(message = "El porcentaje de recargo es obligatorio")
        @DecimalMin(value = "0.01", message = "El porcentaje de recargo debe ser mayor a 0")
        BigDecimal porcentajeRecargo,

        @NotNull(message = "Los días de gracia son obligatorios")
        @Min(value = 0, message = "Los días de gracia no pueden ser negativos")
        Short diasGracia,

        String fundamentoLegal,

        @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
        LocalDate vigenteDesde,

        LocalDate vigenteHasta,

        @Pattern(regexp = "^(SIMPLE|COMPUESTO)$", message = "El tipo de cálculo debe ser SIMPLE o COMPUESTO")
        String tipoCalculo,

        @DecimalMin(value = "0.01", message = "El tope máximo debe ser mayor a 0")
        BigDecimal topeMaximoPct
) {}
