package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle del rol de sistema")
public record RolResponseDto(
        @Schema(description = "Identificador del rol")
        Short id,

        @Schema(description = "Clave interna del rol", example = "ADMINISTRADOR")
        String clave,

        @Schema(description = "Nombre descriptivo del rol", example = "Administrador de mercado")
        String nombre,

        @Schema(description = "Descripción de responsabilidades")
        String descripcion,

        @Schema(description = "Indica si requiere alcance de mercado o municipio")
        boolean requiereAlcance
) {}
