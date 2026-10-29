package mx.gob.chiapas.gestamercado.api.domain.catalogos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle del giro comercial")
public record GiroComercialDto(
        Long id,
        String clave,
        String nombre,
        boolean activo
) {}
