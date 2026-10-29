package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de rotación y renovación de tokens mediante Refresh Token")
public record RefreshTokenRequestDto(
        @Schema(description = "Token de refresco activo emitido previamente", example = "a1b2c3d4e5f6...")
        @NotBlank(message = "El refresh token no puede estar vacío")
        String refreshToken
) {}
