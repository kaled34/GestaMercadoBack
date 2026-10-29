package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para configuración de segundo factor de autenticación (MFA)")
public record ConfigureMfaRequestDto(
        @Schema(description = "Indica si se activa o desactiva MFA")
        boolean habilitado,

        @Schema(description = "Secreto MFA cifrado en Base64 (requerido si habilitado es true)")
        String secretoCifradoBase64
) {}
