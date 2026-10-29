package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CargoAjusteDto(
        @NotNull(message = "El nuevo monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El nuevo monto debe ser mayor a 0")
        BigDecimal nuevoMonto,

        @NotBlank(message = "El motivo de ajuste es obligatorio")
        String motivo
) {}
