package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoTramite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TraspasoCreateDto(
        @NotNull(message = "El ID del local comercial es obligatorio")
        UUID localId,

        @NotNull(message = "El ID de la concesión de origen es obligatorio")
        UUID concesionOrigenId,

        @NotNull(message = "El ID de la persona de origen es obligatorio")
        UUID personaOrigenId,

        @NotNull(message = "El ID de la persona de destino es obligatorio")
        UUID personaDestinoId,

        @NotNull(message = "El tipo de trámite es obligatorio (FORMAL o INFORMAL_NOTIFICADO)")
        TipoTramite tipoTramite,

        LocalDate fechaSolicitud,
        LocalDate fechaNotificacion,
        String notas,
        BigDecimal cuotaBaseUma
) {}
