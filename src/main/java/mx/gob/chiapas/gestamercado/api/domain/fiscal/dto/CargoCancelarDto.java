package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.NotBlank;

public record CargoCancelarDto(
        @NotBlank(message = "El motivo de cancelación es obligatorio")
        String motivo
) {}
