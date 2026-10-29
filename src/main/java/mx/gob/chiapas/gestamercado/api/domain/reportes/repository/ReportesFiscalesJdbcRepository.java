package mx.gob.chiapas.gestamercado.api.domain.reportes.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AdeudoLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AlertaOperativaDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.EfectivoPendienteDepositoDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.LocalCuotaActualDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.PagoSinCorteDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.SaldoFavorDto;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReportesFiscalesJdbcRepository {

    private final JdbcClient jdbcClient;

    public ReportesFiscalesJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<AdeudoLocalDto> listarAdeudosMercado(Long mercadoId) {
        return jdbcClient.sql("""
                SELECT local_id,
                       mercado_id,
                       numero_local,
                       titular_persona_id,
                       cargos_vencidos,
                       saldo_adeudado,
                       adeudo_desde,
                       deudor_identificado,
                       saldo_favor_local,
                       saldo_neto_exigible,
                       es_titular_principal
                  FROM gesta.v_adeudos
                 WHERE mercado_id = :mercadoId
                 ORDER BY saldo_neto_exigible DESC
                """)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> new AdeudoLocalDto(
                        (UUID) rs.getObject("local_id"),
                        rs.getLong("mercado_id"),
                        rs.getString("numero_local"),
                        (UUID) rs.getObject("titular_persona_id"),
                        rs.getLong("cargos_vencidos"),
                        rs.getBigDecimal("saldo_adeudado"),
                        rs.getDate("adeudo_desde") != null ? rs.getDate("adeudo_desde").toLocalDate() : null,
                        (UUID) rs.getObject("deudor_identificado"),
                        rs.getBigDecimal("saldo_favor_local"),
                        rs.getBigDecimal("saldo_neto_exigible"),
                        rs.getBoolean("es_titular_principal")
                ))
                .list();
    }

    public Optional<AdeudoLocalDto> obtenerAdeudoLocal(UUID localId) {
        return jdbcClient.sql("""
                SELECT local_id,
                       mercado_id,
                       numero_local,
                       titular_persona_id,
                       cargos_vencidos,
                       saldo_adeudado,
                       adeudo_desde,
                       deudor_identificado,
                       saldo_favor_local,
                       saldo_neto_exigible,
                       es_titular_principal
                  FROM gesta.v_adeudos
                 WHERE local_id = :localId
                """)
                .param("localId", localId)
                .query((rs, rowNum) -> new AdeudoLocalDto(
                        (UUID) rs.getObject("local_id"),
                        rs.getLong("mercado_id"),
                        rs.getString("numero_local"),
                        (UUID) rs.getObject("titular_persona_id"),
                        rs.getLong("cargos_vencidos"),
                        rs.getBigDecimal("saldo_adeudado"),
                        rs.getDate("adeudo_desde") != null ? rs.getDate("adeudo_desde").toLocalDate() : null,
                        (UUID) rs.getObject("deudor_identificado"),
                        rs.getBigDecimal("saldo_favor_local"),
                        rs.getBigDecimal("saldo_neto_exigible"),
                        rs.getBoolean("es_titular_principal")
                ))
                .optional();
    }

    public Optional<SaldoFavorDto> obtenerSaldoFavorLocal(UUID localId) {
        return jdbcClient.sql("""
                SELECT local_id, saldo_favor, titular_persona_id
                  FROM gesta.v_saldo_favor
                 WHERE local_id = :localId
                """)
                .param("localId", localId)
                .query((rs, rowNum) -> new SaldoFavorDto(
                        (UUID) rs.getObject("local_id"),
                        rs.getBigDecimal("saldo_favor"),
                        (UUID) rs.getObject("titular_persona_id")
                ))
                .optional();
    }

    public List<EfectivoPendienteDepositoDto> listarEfectivoPendienteDeposito(Long mercadoId) {
        return jdbcClient.sql("""
                SELECT mercado_id, cobrador_id, pagos, monto, mas_antiguo
                  FROM gesta.v_efectivo_pendiente_deposito
                 WHERE (:mercadoId IS NULL OR mercado_id = :mercadoId)
                """)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("mas_antiguo");
                    return new EfectivoPendienteDepositoDto(
                            rs.getLong("mercado_id"),
                            (UUID) rs.getObject("cobrador_id"),
                            rs.getLong("pagos"),
                            rs.getBigDecimal("monto"),
                            ts != null ? ts.toInstant() : null
                    );
                })
                .list();
    }

    public List<PagoSinCorteDto> listarPagosSinCorte(Long mercadoId) {
        return jdbcClient.sql("""
                SELECT mercado_id, cobrador_id, fecha_local, pagos, monto
                  FROM gesta.v_pagos_sin_corte
                 WHERE (:mercadoId IS NULL OR mercado_id = :mercadoId)
                """)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> new PagoSinCorteDto(
                        rs.getLong("mercado_id"),
                        (UUID) rs.getObject("cobrador_id"),
                        rs.getDate("fecha_local").toLocalDate(),
                        rs.getLong("pagos"),
                        rs.getBigDecimal("monto")
                ))
                .list();
    }

    public Optional<LocalCuotaActualDto> obtenerCuotaActualLocal(UUID localId) {
        return jdbcClient.sql("""
                SELECT local_id,
                       mercado_id,
                       numero_local,
                       tipo_espacio,
                       area_m2,
                       tarifa_id,
                       metodo,
                       valor,
                       unidad,
                       uma_aplicada,
                       periodicidad,
                       cuota_por_unidad
                  FROM gesta.v_local_cuota_actual
                 WHERE local_id = :localId
                """)
                .param("localId", localId)
                .query((rs, rowNum) -> {
                    String tipoEspStr = rs.getString("tipo_espacio");
                    String metodoStr = rs.getString("metodo");
                    String unidadStr = rs.getString("unidad");
                    String perStr = rs.getString("periodicidad");
                    return new LocalCuotaActualDto(
                            (UUID) rs.getObject("local_id"),
                            rs.getLong("mercado_id"),
                            rs.getString("numero_local"),
                            tipoEspStr != null ? TipoEspacio.valueOf(tipoEspStr) : null,
                            rs.getBigDecimal("area_m2"),
                            (Long) rs.getObject("tarifa_id"),
                            metodoStr != null ? MetodoCalculo.valueOf(metodoStr) : null,
                            rs.getBigDecimal("valor"),
                            unidadStr != null ? UnidadValor.valueOf(unidadStr) : null,
                            rs.getBigDecimal("uma_aplicada"),
                            perStr != null ? Periodicidad.valueOf(perStr) : null,
                            rs.getBigDecimal("cuota_por_unidad")
                    );
                })
                .optional();
    }

    public List<AlertaOperativaDto> listarAlertasPadron(Long mercadoId) {
        return jdbcClient.sql("""
                SELECT alerta, mercado_id, local_id, detalle
                  FROM gesta.v_alertas_padron
                 WHERE (:mercadoId IS NULL OR mercado_id = :mercadoId)
                """)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> new AlertaOperativaDto(
                        rs.getString("alerta"),
                        rs.getLong("mercado_id"),
                        (UUID) rs.getObject("local_id"),
                        rs.getString("detalle")
                ))
                .list();
    }

    public List<AlertaOperativaDto> listarAlertasCobro(Long mercadoId) {
        return jdbcClient.sql("""
                SELECT alerta, mercado_id, local_id, detalle
                  FROM gesta.v_alertas_cobro
                 WHERE (:mercadoId IS NULL OR mercado_id = :mercadoId)
                """)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> new AlertaOperativaDto(
                        rs.getString("alerta"),
                        rs.getLong("mercado_id"),
                        (UUID) rs.getObject("local_id"),
                        rs.getString("detalle")
                ))
                .list();
    }
}
