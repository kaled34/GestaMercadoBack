package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CargoGenerarLoteDto(
        @NotNull(message = "El ID del mercado es obligatorio")
        Long mercadoId,

        @NotNull(message = "La fecha de inicio de periodo es obligatoria")
        LocalDate inicio,

        @NotNull(message = "La fecha de fin de periodo es obligatoria")
        LocalDate fin
) {}
