package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import java.time.Instant;
import java.util.UUID;

public record ConcesionSuplenteDto(
        UUID id,
        UUID concesionId,
        UUID personaId,
        String parentesco,
        Instant creadoEn
) {}
