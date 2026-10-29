package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para asignación de un rol a un usuario")
public record UsuarioRolAssignDto(
        @Schema(description = "Identificador del rol a asignar")
        @NotNull(message = "El identificador del rol es obligatorio")
        Short rolId,

        @Schema(description = "Alcance municipal (excluyente con mercado)")
        Long municipioId,

        @Schema(description = "Alcance de mercado (excluyente con municipio)")
        Long mercadoId
) {}
