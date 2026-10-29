package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TraspasoAprobarDto(
        @NotBlank(message = "El número de expediente es obligatorio para dictaminar la aprobación")
        String numeroExpediente,

        @NotNull(message = "La fecha de dictamen es obligatoria")
        LocalDate fechaDictamen,

        LocalDate fechaEfectiva,
        Boolean regularizado
) {}
