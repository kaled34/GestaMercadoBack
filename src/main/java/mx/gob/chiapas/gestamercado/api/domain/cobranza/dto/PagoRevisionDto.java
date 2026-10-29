package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import jakarta.validation.constraints.NotBlank;

public record PagoRevisionDto(
        @NotBlank(message = "El motivo o dictamen de resolución de revisión es obligatorio")
        String motivo
) {}
