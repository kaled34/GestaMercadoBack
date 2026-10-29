package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DepositoHaciendaRegistrarDto(
        @NotNull(message = "El mercado es obligatorio")
        Long mercadoId,

        @NotNull(message = "La fecha del depósito es obligatoria")
        LocalDate fechaDeposito,

        @NotNull(message = "El monto depositado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto depositado debe ser mayor a 0")
        BigDecimal montoDepositado,

        String referencia,

        @NotEmpty(message = "Debe vincular al menos un pago al depósito bancario/hacienda")
        List<UUID> pagosIds,

        LocalDate periodoInicio,
        LocalDate periodoFin,
        String justificacionDiferencia
) {}
