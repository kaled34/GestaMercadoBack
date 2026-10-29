package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoConcesion;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ConcesionDto(
        UUID id,
        UUID localId,
        UUID titularPersonaId,
        Long giroId,
        EstadoConcesion estado,
        Boolean esFormal,
        String numeroExpediente,
        LocalDate fechaDictamen,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String observaciones,
        Instant creadoEn,
        UUID ocupantePersonaId,
        Boolean esTemporalFeria,
        Long mercadoId,
        Long municipioId
) {}
