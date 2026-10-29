package mx.gob.chiapas.gestamercado.api.domain.reportes.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;

import java.math.BigDecimal;
import java.util.UUID;

public record LocalCuotaActualDto(
        UUID localId,
        Long mercadoId,
        String numeroLocal,
        TipoEspacio tipoEspacio,
        BigDecimal areaM2,
        Long tarifaId,
        MetodoCalculo metodo,
        BigDecimal valor,
        UnidadValor unidad,
        BigDecimal umaAplicada,
        Periodicidad periodicidad,
        BigDecimal cuotaPorUnidad
) {}
