package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Respuesta de autenticación exitosa con tokens y metadatos de sesión")
public record LoginResponseDto(
        @Schema(description = "Token de acceso Bearer JWT firmado")
        String accessToken,

        @Schema(description = "Token opaco de refresco para rotación y sesiones offline")
        String refreshToken,

        @Schema(description = "Tiempo de vida del token de acceso en milisegundos", example = "1800000")
        long expiresInMs,

        @Schema(description = "UUID único del usuario autenticado")
        UUID usuarioId,

        @Schema(description = "Nombre de usuario")
        String username,

        @Schema(description = "Indica si el usuario debe cambiar su contraseña obligatoriamente")
        boolean debeCambiarPass,

        @Schema(description = "Indica si el usuario requiere verificación de segundo factor MFA")
        boolean mfaRequerido,

        @Schema(description = "Claves de los roles asignados al usuario")
        List<String> roles,

        @Schema(description = "Identificador del municipio si el rol tiene alcance municipal")
        Long municipioId,

        @Schema(description = "Identificador del mercado si el rol tiene alcance de mercado")
        Long mercadoId
) {}
