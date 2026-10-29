package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Día inhábil o festivo oficial registrado para un mercado")
public record MercadoDiaInhabilDto(
        Long mercadoId,
        LocalDate fecha,
        String motivo,
        UUID registradoPor,
        Instant creadoEn
) {}
