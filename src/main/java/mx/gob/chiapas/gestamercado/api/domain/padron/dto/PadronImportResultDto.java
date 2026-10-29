package mx.gob.chiapas.gestamercado.api.domain.padron.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado del procesamiento de importación masiva de padrón")
public record PadronImportResultDto(
        int totalFilas,
        int procesadosExitosos,
        int omitidosOErrores,
        List<String> mensajes
) {}
