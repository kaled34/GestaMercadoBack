package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.ConceptoCargo;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CargoDto(
        UUID id,
        UUID localId,
        UUID concesionId,
        Long tarifaId,
        UUID cargoOrigenId,
        ConceptoCargo concepto,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        BigDecimal areaM2Aplicada,
        BigDecimal metrosLinealesAplicados,
        MetodoCalculo metodoCalculo,
        BigDecimal valorTarifaAplicado,
        UnidadValor unidadValorAplicada,
        BigDecimal valorUmaAplicado,
        BigDecimal cuotaUnidadAplicada,
        Periodicidad periodicidad,
        BigDecimal unidades,
        BigDecimal montoTotal,
        BigDecimal montoPagado,
        EstadoCargo estado,
        Instant generadoEn,
        String motivoCancelacion,
        UUID canceladoPor,
        Instant canceladoEn
) {
    public BigDecimal saldoPendiente() {
        if (montoTotal == null) return BigDecimal.ZERO;
        BigDecimal pagado = montoPagado != null ? montoPagado : BigDecimal.ZERO;
        return montoTotal.subtract(pagado);
    }
}
