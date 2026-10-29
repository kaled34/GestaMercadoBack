package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Solicitud de creación de un nuevo usuario")
public record UsuarioCreateDto(
        @Schema(description = "Nombre de usuario único", example = "cobrador.zona1")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña inicial en texto plano (mínimo 8 caracteres)", example = "Chiapas#2026")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @Schema(description = "UUID de la persona física o moral vinculada en gesta.persona (opcional)")
        UUID personaId,

        @Schema(description = "Identificador del rol inicial a asignar")
        Short rolInicialId,

        @Schema(description = "Alcance municipal para el rol inicial (opcional)")
        Long municipioId,

        @Schema(description = "Alcance de mercado para el rol inicial (opcional)")
        Long mercadoId
) {}
