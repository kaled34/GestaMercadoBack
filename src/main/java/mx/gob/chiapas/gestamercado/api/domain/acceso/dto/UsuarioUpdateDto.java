package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Solicitud de modificación de estado o persona vinculada de un usuario")
public record UsuarioUpdateDto(
        @Schema(description = "Nuevo estado de activación de la cuenta")
        Boolean activo,

        @Schema(description = "Nuevo identificador de persona vinculada")
        UUID personaId
) {}
