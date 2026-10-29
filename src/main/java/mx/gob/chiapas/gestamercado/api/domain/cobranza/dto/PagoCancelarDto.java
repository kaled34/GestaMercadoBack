package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.NotBlank;

public record PagoCancelarDto(
        @NotBlank(message = "El motivo de cancelación es obligatorio")
        String motivo
) {}
