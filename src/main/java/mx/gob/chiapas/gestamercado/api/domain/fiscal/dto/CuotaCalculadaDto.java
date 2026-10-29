package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;

import java.math.BigDecimal;

public record CuotaCalculadaDto(
        Long tarifaId,
        BigDecimal areaM2,
        BigDecimal valor,
        UnidadValor unidad,
        BigDecimal umaAplicada,
        MetodoCalculo metodo,
        Periodicidad periodicidad,
        BigDecimal monto,
        Boolean incluirEnLote,
        BigDecimal metrosLineales
) {}
