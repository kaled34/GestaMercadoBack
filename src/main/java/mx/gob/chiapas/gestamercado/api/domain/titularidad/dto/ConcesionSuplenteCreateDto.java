package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record ConcesionSuplenteCreateDto(
        @NotNull(message = "El ID de la concesión es obligatorio")
        UUID concesionId,

        @NotNull(message = "El ID de la persona suplente es obligatorio")
        UUID personaId,

        @NotBlank(message = "El parentesco es obligatorio")
        @Pattern(regexp = "(?i)^(PADRE|MADRE|HIJO|HIJA|CONYUGE)$", message = "El parentesco debe ser PADRE, MADRE, HIJO, HIJA o CONYUGE")
        String parentesco
) {}
