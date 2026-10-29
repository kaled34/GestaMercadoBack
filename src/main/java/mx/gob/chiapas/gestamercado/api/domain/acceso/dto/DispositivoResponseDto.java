package mx.gob.chiapas.gestamercado.api.domain.acceso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detalle de un dispositivo de cobro enrolado")
public record DispositivoResponseDto(
        @Schema(description = "UUID único del dispositivo")
        UUID id,

        @Schema(description = "UUID del usuario asignado")
        UUID usuarioId,

        @Schema(description = "Identificador del mercado asignado")
        Long mercadoId,

        @Schema(description = "Alias amigable del dispositivo")
        String alias,

        @Schema(description = "Modelo del dispositivo")
        String modelo,

        @Schema(description = "Serie asignada para los folios de cobro")
        String serieFolio,

        @Schema(description = "Fecha y hora de la última sincronización offline-first")
        Instant ultimaSincronizacion,

        @Schema(description = "Estado de activación")
        boolean activo,

        @Schema(description = "Último consecutivo emitido por el dispositivo")
        long ultimoConsecutivo,

        @Schema(description = "Clave simétrica HMAC-SHA256 en formato hexadecimal para enrolamiento en la app móvil")
        String secretoHmacHex
) {}
