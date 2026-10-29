package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Detalle del valor diario de UMA configurado para un mercado")
public record UmaMercadoDto(
        Long mercadoId,
        LocalDate vigenteDesde,
        BigDecimal valorDiario,
        String fuente,
        UUID registradoPor,
        Instant registradoEn
) {}
