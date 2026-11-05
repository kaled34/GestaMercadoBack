package mx.gob.chiapas.gestamercado.api.domain.cobranza.repository;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaCerrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaRegistrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.JornadaSinLiquidarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCobroCampoDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.CanalCobro;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class CobranzaJdbcRepository {

    private final JdbcClient jdbcClient;
    private final DataSource dataSource;

    public CobranzaJdbcRepository(JdbcClient jdbcClient, DataSource dataSource) {
        this.jdbcClient = jdbcClient;
        this.dataSource = dataSource;
    }

    /**
     * Registra un cobro realizado en campo por inspector o cobrador móvil con validación
     * criptográfica de hash offline HMAC-SHA256 y geolocalización PostGIS.
     */
    public UUID registrarCobroCampo(PagoCobroCampoDto dto) {
        Double lon = dto.ubicacion() != null ? dto.ubicacion().longitude() : null;
        Double lat = dto.ubicacion() != null ? dto.ubicacion().latitude() : null;
        String forma = (dto.formaPago() != null ? dto.formaPago() : FormaPago.EFECTIVO).name();
        BigDecimal unidades = dto.unidades() != null ? dto.unidades() : BigDecimal.ONE;
        Timestamp tsFecha = Timestamp.from(dto.fecha());

        return jdbcClient.sql("""
                SELECT gesta.fn_registrar_cobro_campo(
                    :pagoId,
                    :localId,
                    :cobradorId,
                    :dispositivoId,
                    :folio,
                    :montoCobrado,
                    :fecha,
                    :hashOffline,
                    :unidades,
                    CAST(:formaPago AS gesta.forma_pago),
                    CASE WHEN :lon IS NOT NULL AND :lat IS NOT NULL
                         THEN public.ST_SetSRID(public.ST_MakePoint(:lon, :lat), 4326)
                         ELSE NULL END,
                    :fechaInicioCargo
                )
                """)
                .param("pagoId", dto.pagoId())
                .param("localId", dto.localId())
                .param("cobradorId", dto.cobradorId())
                .param("dispositivoId", dto.dispositivoId())
                .param("folio", dto.folio())
                .param("montoCobrado", dto.montoCobrado())
                .param("fecha", tsFecha)
                .param("hashOffline", dto.hashOffline())
                .param("unidades", unidades)
                .param("formaPago", forma)
                .param("lon", lon)
                .param("lat", lat)
                .param("fechaInicioCargo", dto.fechaInicioCargo())
                .query(UUID.class)
                .single();
    }

    /**
     * Registra un pago cobrado directamente en caja o ventanilla fiscal.
     */
    public UUID registrarPagoCaja(PagoCajaDto dto) {
        String forma = (dto.formaPago() != null ? dto.formaPago() : FormaPago.EFECTIVO).name();
        String canal = (dto.canal() != null ? dto.canal() : CanalCobro.CAJA_POLITICA_FISCAL).name();
        Timestamp tsFecha = dto.fecha() != null ? Timestamp.from(dto.fecha()) : new Timestamp(System.currentTimeMillis());

        return jdbcClient.sql("""
                SELECT gesta.fn_registrar_pago_caja(
                    CAST(:localId AS uuid),
                    CAST(:folio AS text),
                    :monto,
                    CAST(:fecha AS timestamptz),
                    CAST(:formaPago AS gesta.forma_pago),
                    CAST(:canal AS gesta.canal_cobro),
                    CAST(:loteId AS uuid),
                    CAST(:hashFila AS text)
                )
                """)
                .param("localId", dto.localId())
                .param("folio", dto.folio())
                .param("monto", dto.monto())
                .param("fecha", tsFecha)
                .param("formaPago", forma)
                .param("canal", canal)
                .param("loteId", dto.loteId())
                .param("hashFila", dto.hashFila())
                .query(UUID.class)
                .single();
    }

    /**
     * Aplica o amortiza un monto de pago contra un cargo específico.
     */
    public String aplicarPago(UUID pagoId, UUID cargoId, BigDecimal monto) {
        return jdbcClient.sql("""
                SELECT gesta.fn_aplicar_pago(:pagoId, :cargoId, :monto)
                """)
                .param("pagoId", pagoId)
                .param("cargoId", cargoId)
                .param("monto", monto)
                .query(String.class)
                .single();
    }

    /**
     * Aplica automáticamente saldo a favor remanente a los adeudos pendientes de un local.
     */
    public BigDecimal aplicarSaldoFavorLocal(UUID localId, UUID concesionId) {
        return jdbcClient.sql("""
                SELECT gesta.fn_aplicar_saldo_favor_local(:localId, :concesionId)
                """)
                .param("localId", localId)
                .param("concesionId", concesionId)
                .query(BigDecimal.class)
                .single();
    }

    /**
     * Cancela un pago con motivo auditable.
     */
    public void cancelarPago(UUID pagoId, String motivo) {
        jdbcClient.sql("""
                SELECT gesta.fn_cancelar_pago(:pagoId, :motivo)
                """)
                .param("pagoId", pagoId)
                .param("motivo", motivo)
                .query()
                .singleRow();
    }

    /**
     * Resuelve el estado de revisión de un pago en conflicto.
     */
    public void resolverRevisionPago(UUID pagoId, String motivo) {
        jdbcClient.sql("""
                SELECT gesta.fn_resolver_revision_pago(:pagoId, :motivo)
                """)
                .param("pagoId", pagoId)
                .param("motivo", motivo)
                .query()
                .singleRow();
    }

    /**
     * Cierra el corte ciego diario de caja declarando la recaudación.
     */
    public UUID cerrarCorteCaja(CorteCajaCerrarDto dto) {
        return jdbcClient.sql("""
                SELECT gesta.fn_cerrar_corte(
                    :cobradorId,
                    :mercadoId,
                    :fecha,
                    :montoDeclarado,
                    :observaciones
                )
                """)
                .param("cobradorId", dto.cobradorId())
                .param("mercadoId", dto.mercadoId())
                .param("fecha", dto.fecha())
                .param("montoDeclarado", dto.montoDeclarado())
                .param("observaciones", dto.observaciones())
                .query(UUID.class)
                .single();
    }

    /**
     * Consulta las jornadas y días de cobro sin corte cerrado por parte del cobrador.
     */
    public List<JornadaSinLiquidarDto> listarJornadasSinLiquidar(UUID cobradorId, Long mercadoId) {
        return jdbcClient.sql("""
                SELECT fecha_local, pagos, monto
                  FROM gesta.fn_jornadas_sin_liquidar(:cobradorId, :mercadoId)
                """)
                .param("cobradorId", cobradorId)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> new JornadaSinLiquidarDto(
                        rs.getDate("fecha_local").toLocalDate(),
                        rs.getLong("pagos"),
                        rs.getBigDecimal("monto")
                ))
                .list();
    }

    /**
     * Verifica si el cobrador tiene autorización para abrir una nueva jornada de cobro
     * (sin cortes pendientes que excedan la tolerancia).
     */
    public Boolean puedeAbrirJornada(UUID cobradorId, Long mercadoId) {
        return jdbcClient.sql("""
                SELECT gesta.fn_puede_abrir_jornada(:cobradorId, :mercadoId)
                """)
                .param("cobradorId", cobradorId)
                .param("mercadoId", mercadoId)
                .query(Boolean.class)
                .single();
    }

    /**
     * Registra un depósito bancario o a la tesorería/hacienda municipal conciliando los pagos.
     */
    public UUID registrarDepositoHacienda(DepositoHaciendaRegistrarDto dto) {
        try (Connection conn = dataSource.getConnection()) {
            Array sqlArray = conn.createArrayOf("uuid", dto.pagosIds().toArray());
            return jdbcClient.sql("""
                    SELECT gesta.fn_registrar_deposito_hacienda(
                        :mercadoId,
                        :fechaDeposito,
                        :montoDepositado,
                        :referencia,
                        :pagosIds,
                        :periodoInicio,
                        :periodoFin,
                        :justificacionDiferencia
                    )
                    """)
                    .param("mercadoId", dto.mercadoId())
                    .param("fechaDeposito", dto.fechaDeposito())
                    .param("montoDepositado", dto.montoDepositado())
                    .param("referencia", dto.referencia())
                    .param("pagosIds", sqlArray)
                    .param("periodoInicio", dto.periodoInicio())
                    .param("periodoFin", dto.periodoFin())
                    .param("justificacionDiferencia", dto.justificacionDiferencia())
                    .query(UUID.class)
                    .single();
        } catch (SQLException e) {
            throw new RuntimeException("Error creando arreglo JDBC para depósito: " + e.getMessage(), e);
        }
    }
}
