package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Solicitud de inicio de sesión")
public record LoginRequestDto(
        @Schema(description = "Nombre de usuario único", example = "admin.mercado1")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña en texto plano a verificar mediante Argon2id", example = "Chiapas2026*Seguro")
        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @Schema(description = "UUID del dispositivo físico de cobro (opcional si inicia sesión en web)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID dispositivoId
) {}
