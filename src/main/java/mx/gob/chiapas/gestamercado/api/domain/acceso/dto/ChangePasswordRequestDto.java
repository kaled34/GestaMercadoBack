package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Solicitud para actualización de contraseña")
public record ChangePasswordRequestDto(
        @Schema(description = "UUID del usuario objetivo (opcional si es el mismo usuario autenticado)")
        UUID usuarioId,

        @Schema(description = "Contraseña actual (obligatoria para autogestión)")
        String currentPassword,

        @Schema(description = "Nueva contraseña a encriptar con Argon2id (mínimo 8 caracteres)")
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        String newPassword
) {}
