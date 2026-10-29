package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Detalle del valor UMA oficial nacional (INEGI)")
public record UmaOficialDto(
        LocalDate vigenteDesde,
        BigDecimal valorDiario,
        String fuente
) {}
