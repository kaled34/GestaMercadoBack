package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de alta de giro comercial")
public record GiroComercialCreateDto(
        @NotBlank(message = "La clave del giro es obligatoria")
        String clave,

        @NotBlank(message = "El nombre del giro es obligatorio")
        String nombre
) {}
