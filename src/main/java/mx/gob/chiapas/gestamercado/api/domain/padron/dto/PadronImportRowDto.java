package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Fila para importación masiva de padrón de locales y concesionarios")
public record PadronImportRowDto(
        String numeroLocal,
        String nombreComercial,
        String folioTarjeton,
        String tipoEspacio,
        String estatus,
        String giroClave,
        BigDecimal areaOficialM2,
        BigDecimal metrosLineales,
        String curpTitular,
        String rfcTitular,
        String nombreTitular
) {}
