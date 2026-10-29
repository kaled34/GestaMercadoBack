package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import java.util.UUID;

public record AlertaOperativaDto(
        String alerta,
        Long mercadoId,
        UUID localId,
        String detalle
) {}
