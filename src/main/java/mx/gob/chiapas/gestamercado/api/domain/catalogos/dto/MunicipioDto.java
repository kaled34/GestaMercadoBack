package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle del municipio")
public record MunicipioDto(
        Long id,
        String claveInegi,
        String nombre,
        String estado,
        String zonaHoraria,
        short diasMaxAntiguedadCobro,
        boolean activo
) {}
