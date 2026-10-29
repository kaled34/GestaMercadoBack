package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detalle público de un usuario del sistema (sin exponer hashes ni secretos)")
public record UsuarioResponseDto(
        @Schema(description = "UUID único del usuario")
        UUID id,

        @Schema(description = "UUID de la persona asociada en el padrón")
        UUID personaId,

        @Schema(description = "Nombre de usuario")
        String username,

        @Schema(description = "Estado de actividad de la cuenta")
        boolean activo,

        @Schema(description = "Indica si debe cambiar contraseña")
        boolean debeCambiarPass,

        @Schema(description = "Contador de intentos fallidos consecutivos de autenticación")
        short intentosFallidos,

        @Schema(description = "Fecha hasta la cual la cuenta permanece bloqueada temporalmente")
        Instant bloqueadoHasta,

        @Schema(description = "Indica si tiene activado el segundo factor de autenticación")
        boolean mfaHabilitado,

        @Schema(description = "Fecha y hora del último acceso exitoso")
        Instant ultimoAcceso,

        @Schema(description = "UUID del usuario que creó esta cuenta")
        UUID creadoPor,

        @Schema(description = "Fecha y hora de creación de la cuenta")
        Instant creadoEn,

        @Schema(description = "Lista de roles y alcances activos asignados al usuario")
        List<UsuarioRolResponseDto> roles
) {}
