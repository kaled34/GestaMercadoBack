package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AdeudoLocalDto(
        UUID localId,
        Long mercadoId,
        String numeroLocal,
        UUID titularPersonaId,
        Long cargosVencidos,
        BigDecimal saldoAdeudado,
        LocalDate adeudoDesde,
        UUID deudorIdentificado,
        BigDecimal saldoFavorLocal,
        BigDecimal saldoNetoExigible,
        Boolean esTitularPrincipal
) {}
