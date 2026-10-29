package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Solicitud para registrar un día inhábil")
public record MercadoDiaInhabilCreateDto(
        @NotNull(message = "El identificador del mercado es obligatorio")
        Long mercadoId,

        @NotNull(message = "La fecha inhábil es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo
) {}
