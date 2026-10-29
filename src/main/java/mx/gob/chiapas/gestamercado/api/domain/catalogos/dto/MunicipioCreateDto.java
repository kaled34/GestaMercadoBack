package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Solicitud de alta de municipio")
public record MunicipioCreateDto(
        @Schema(description = "Clave INEGI de 5 dígitos", example = "07101")
        @NotBlank(message = "La clave INEGI es obligatoria")
        @Pattern(regexp = "^[0-9]{5}$", message = "La clave INEGI debe contener exactamente 5 dígitos numéricos")
        String claveInegi,

        @Schema(description = "Nombre oficial del municipio", example = "Tuxtla Gutiérrez")
        @NotBlank(message = "El nombre del municipio es obligatorio")
        String nombre,

        @Schema(description = "Estado", example = "Chiapas")
        String estado,

        @Schema(description = "Zona horaria", example = "America/Mexico_City")
        String zonaHoraria,

        @Schema(description = "Días máximos de antigüedad para registrar cobros en campo", example = "15")
        @Positive(message = "Los días máximos de antigüedad deben ser mayores a 0")
        short diasMaxAntiguedadCobro
) {}
