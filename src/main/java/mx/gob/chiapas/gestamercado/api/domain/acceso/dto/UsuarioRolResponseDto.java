package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detalle del rol asignado a un usuario con su alcance")
public record UsuarioRolResponseDto(
        @Schema(description = "Identificador de la asignación")
        UUID id,

        @Schema(description = "Identificador del rol")
        Short rolId,

        @Schema(description = "Clave del rol", example = "ADMINISTRADOR")
        String rolClave,

        @Schema(description = "Nombre del rol")
        String rolNombre,

        @Schema(description = "Identificador del municipio si tiene alcance municipal")
        Long municipioId,

        @Schema(description = "Identificador del mercado si tiene alcance de mercado")
        Long mercadoId,

        @Schema(description = "Estado de la asignación")
        boolean activo,

        @Schema(description = "Fecha de asignación")
        Instant asignadoEn
) {}
