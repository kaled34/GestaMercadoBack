package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con nuevos tokens de acceso y refresco")
public record TokenResponseDto(
        @Schema(description = "Nuevo token Bearer JWT de acceso")
        String accessToken,

        @Schema(description = "Nuevo refresh token rotado")
        String refreshToken,

        @Schema(description = "Tiempo de expiración en milisegundos")
        long expiresInMs
) {}
