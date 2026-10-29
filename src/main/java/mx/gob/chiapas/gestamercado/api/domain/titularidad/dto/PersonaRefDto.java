package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import java.util.UUID;

public record PersonaRefDto(
        UUID id,
        String nombre
) {}
