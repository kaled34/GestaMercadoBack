package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoConcesion;

import java.time.LocalDate;
import java.util.UUID;

public record ConcesionUpdateDto(
        Long giroId,
        EstadoConcesion estado,
        Boolean esFormal,
        String numeroExpediente,
        LocalDate fechaDictamen,
        LocalDate fechaFin,
        String observaciones,
        UUID ocupantePersonaId
) {}
