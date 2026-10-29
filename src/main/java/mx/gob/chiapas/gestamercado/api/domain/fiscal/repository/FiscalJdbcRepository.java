package mx.gob.chiapas.gestamercado.api.domain.fiscal.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CuotaCalculadaDto;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FiscalJdbcRepository {

    private final JdbcClient jdbcClient;

    public FiscalJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Resuelve la tarifa y calcula la cuota aplicable a un local comercial en una fecha
     * invocando gesta.fn_calcular_cuota(p_local, p_fecha).
     */
    public Optional<CuotaCalculadaDto> calcularCuota(UUID localId, LocalDate fecha) {
        return jdbcClient.sql("""
                SELECT tarifa_id,
                       area_m2,
                       valor,
                       unidad,
                       uma_aplicada,
                       metodo,
                       periodicidad,
                       monto,
                       incluir_en_lote,
                       metros_lineales
                  FROM gesta.fn_calcular_cuota(:localId, :fecha)
                """)
                .param("localId", localId)
                .param("fecha", fecha)
                .query((rs, rowNum) -> {
                    Long tarifaId = (Long) rs.getObject("tarifa_id");
                    if (tarifaId == null) return null;
                    String unidadStr = rs.getString("unidad");
                    String metodoStr = rs.getString("metodo");
                    String periodicidadStr = rs.getString("periodicidad");
                    return new CuotaCalculadaDto(
                            tarifaId,
                            rs.getBigDecimal("area_m2"),
                            rs.getBigDecimal("valor"),
                            unidadStr != null ? UnidadValor.valueOf(unidadStr) : null,
                            rs.getBigDecimal("uma_aplicada"),
                            metodoStr != null ? MetodoCalculo.valueOf(metodoStr) : null,
                            periodicidadStr != null ? Periodicidad.valueOf(periodicidadStr) : null,
                            rs.getBigDecimal("monto"),
                            rs.getBoolean("incluir_en_lote"),
                            rs.getBigDecimal("metros_lineales")
                    );
                })
                .optional();
    }

    /**
     * Genera el cargo ordinario para un local específico e intervalo de fechas.
     */
    public UUID generarCargoLocal(UUID localId, LocalDate inicio, LocalDate fin) {
        return jdbcClient.sql("""
                SELECT gesta.fn_generar_cargo_local(:localId, :inicio, :fin)
                """)
                .param("localId", localId)
                .param("inicio", inicio)
                .param("fin", fin)
                .query(UUID.class)
                .single();
    }

    /**
     * Emite cargos masivos por lote para todos los locales activos del mercado.
     * Retorna el resumen jsonb de cargos generados y errores.
     */
    public String generarCargosMercado(Long mercadoId, LocalDate inicio, LocalDate fin) {
        return jdbcClient.sql("""
                SELECT gesta.fn_generar_cargos(:mercadoId, :inicio, :fin)::text
                """)
                .param("mercadoId", mercadoId)
                .param("inicio", inicio)
                .param("fin", fin)
                .query(String.class)
                .single();
    }

    /**
     * Ejecuta el motor de cálculo y emisión de recargos moratorios para el mercado.
     */
    public Integer generarRecargos(Long mercadoId, LocalDate fecha) {
        return jdbcClient.sql("""
                SELECT gesta.fn_generar_recargos(:mercadoId, :fecha)
                """)
                .param("mercadoId", mercadoId)
                .param("fecha", fecha)
                .query(Integer.class)
                .single();
    }

    /**
     * Aplica la penalización de morosidad diaria a un local determinado.
     */
    public UUID aplicarPenalizacionMorosidadDiaria(UUID localId, LocalDate fecha) {
        return jdbcClient.sql("""
                SELECT gesta.fn_aplicar_penalizacion_morosidad_diaria(:localId, :fecha)
                """)
                .param("localId", localId)
                .param("fecha", fecha)
                .query(UUID.class)
                .single();
    }

    /**
     * Recalcula los importes y cuotas de un cargo pendiente conforme a la tarifa actual.
     */
    public void recalcularCargo(UUID cargoId) {
        jdbcClient.sql("""
                SELECT gesta.fn_recalcular_cargo(:cargoId)
                """)
                .param("cargoId", cargoId)
                .query()
                .singleRow();
    }

    /**
     * Ajusta el monto de un cargo registrando su respectiva justificación y auditoría.
     */
    public void ajustarCargo(UUID cargoId, BigDecimal nuevoMonto, String motivo) {
        jdbcClient.sql("""
                SELECT gesta.fn_ajustar_cargo(:cargoId, :nuevoMonto, :motivo)
                """)
                .param("cargoId", cargoId)
                .param("nuevoMonto", nuevoMonto)
                .param("motivo", motivo)
                .query()
                .singleRow();
    }

    /**
     * Cancela un cargo registrando el motivo en auditoría.
     */
    public void cancelarCargo(UUID cargoId, String motivo) {
        jdbcClient.sql("""
                SELECT gesta.fn_cancelar_cargo(:cargoId, :motivo)
                """)
                .param("cargoId", cargoId)
                .param("motivo", motivo)
                .query()
                .singleRow();
    }

    /**
     * Anula recargos generados indebidamente sobre un cargo base.
     */
    public Integer anularRecargosIndebidos(UUID cargoId, UUID usuarioId) {
        return jdbcClient.sql("""
                SELECT gesta.fn_anular_recargos_indebidos(:cargoId, :usuarioId)
                """)
                .param("cargoId", cargoId)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();
    }
}
