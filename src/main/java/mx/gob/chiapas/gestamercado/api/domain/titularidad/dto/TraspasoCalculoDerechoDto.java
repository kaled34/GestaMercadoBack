package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TraspasoCalculoDerechoDto(
        UUID concesionOrigenId,
        UUID localId,
        Long mercadoId,
        UUID personaDestinoId,
        Boolean esFamiliarSuplente,
        String parentescoRegistrado,
        BigDecimal porcentajeDescuento,
        BigDecimal umaAplicada,
        BigDecimal cuotaBaseUma,
        BigDecimal montoBruto,
        BigDecimal montoBonificacion,
        BigDecimal montoNetoPagar
) {}
