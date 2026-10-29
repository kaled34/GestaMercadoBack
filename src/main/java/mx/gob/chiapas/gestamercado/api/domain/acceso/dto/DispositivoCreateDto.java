package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema(description = "Solicitud de alta y enrolamiento de un dispositivo móvil de cobro")
public record DispositivoCreateDto(
        @Schema(description = "UUID del usuario cobrador o inspector asignado al dispositivo")
        @NotNull(message = "El cobrador asignado es obligatorio")
        UUID usuarioId,

        @Schema(description = "Identificador del mercado donde operará el dispositivo")
        Long mercadoId,

        @Schema(description = "Alias o nombre amigable del dispositivo", example = "Terminal Android Mercado Central 01")
        String alias,

        @Schema(description = "Marca y modelo del dispositivo", example = "Sunmi V2 PRO")
        String modelo,

        @Schema(description = "Serie o prefijo para generación de folios (2 a 10 caracteres alfanuméricos en mayúsculas)", example = "MC01")
        @NotBlank(message = "La serie de folios es obligatoria")
        @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "La serie debe contener entre 2 y 10 caracteres alfanuméricos en mayúsculas")
        String serieFolio
) {}
