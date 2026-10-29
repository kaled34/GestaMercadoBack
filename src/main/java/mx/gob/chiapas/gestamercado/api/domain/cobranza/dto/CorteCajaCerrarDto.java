package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CorteCajaCerrarDto(
        @NotNull(message = "El cobrador es obligatorio")
        UUID cobradorId,

        @NotNull(message = "El mercado es obligatorio")
        Long mercadoId,

        @NotNull(message = "La fecha del corte es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El monto declarado en efectivo/valores es obligatorio (corte ciego)")
        @DecimalMin(value = "0.0", message = "El monto declarado no puede ser negativo")
        BigDecimal montoDeclarado,

        String observaciones
) {}
