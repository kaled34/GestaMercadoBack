package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de modificación de un dispositivo de cobro")
public record DispositivoUpdateDto(
        @Schema(description = "Alias o nombre amigable del dispositivo")
        String alias,

        @Schema(description = "Marca y modelo del dispositivo")
        String modelo,

        @Schema(description = "Estado de activación del dispositivo")
        Boolean activo
) {}
