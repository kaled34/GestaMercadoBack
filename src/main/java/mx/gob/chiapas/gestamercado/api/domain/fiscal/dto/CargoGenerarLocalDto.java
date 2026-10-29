package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CargoGenerarLocalDto(
        @NotNull(message = "El ID del local comercial es obligatorio")
        UUID localId,

        LocalDate inicio,
        LocalDate fin
) {}
