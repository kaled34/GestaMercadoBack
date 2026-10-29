package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ConcesionCreateDto(
        @NotNull(message = "El ID del local comercial es obligatorio")
        UUID localId,

        @NotNull(message = "El ID de la persona titular es obligatorio")
        UUID titularPersonaId,

        @NotNull(message = "El ID del giro comercial es obligatorio")
        Long giroId,

        Boolean esFormal,
        String numeroExpediente,
        LocalDate fechaDictamen,

        @NotNull(message = "La fecha de inicio de concesión es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,
        String observaciones,
        UUID ocupantePersonaId,
        Boolean esTemporalFeria
) {}
