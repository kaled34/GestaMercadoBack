package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PermisoFeriaExpressDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaRefDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCalculoDerechoDto;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TitularidadJdbcRepository {

    private final JdbcClient jdbcClient;

    public TitularidadJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Busca y vincula una persona al padrón del mercado mediante gesta.fn_buscar_vincular_persona(p_curp, p_rfc, p_mercado).
     */
    public Optional<PersonaRefDto> buscarYVincularPersona(String curp, String rfc, Long mercadoId) {
        return jdbcClient.sql("""
                SELECT id, nombre
                  FROM gesta.fn_buscar_vincular_persona(:curp, :rfc, :mercadoId)
                """)
                .param("curp", curp)
                .param("rfc", rfc)
                .param("mercadoId", mercadoId)
                .query((rs, rowNum) -> {
                    UUID id = (UUID) rs.getObject("id");
                    String nombre = rs.getString("nombre");
                    if (id == null) {
                        return null;
                    }
                    return new PersonaRefDto(id, nombre);
                })
                .optional();
    }

    /**
     * Calcula la liquidación del derecho de traspaso conforme a UMA y parentesco familiar de suplencia
     * invocando gesta.fn_calcular_derecho_traspaso(p_concesion_origen_id, p_persona_destino_id, p_cuota_base_uma, p_fecha).
     */
    public TraspasoCalculoDerechoDto calcularDerechoTraspaso(UUID concesionOrigenId,
                                                             UUID personaDestinoId,
                                                             BigDecimal cuotaBaseUma,
                                                             LocalDate fecha) {
        return jdbcClient.sql("""
                SELECT concesion_origen_id,
                       local_id,
                       mercado_id,
                       persona_destino_id,
                       es_familiar_suplente,
                       parentesco_registrado,
                       porcentaje_descuento,
                       uma_aplicada,
                       cuota_base_uma,
                       monto_bruto,
                       monto_bonificacion,
                       monto_neto_pagar
                  FROM gesta.fn_calcular_derecho_traspaso(:concesionOrigenId, :personaDestinoId, :cuotaBaseUma, :fecha)
                """)
                .param("concesionOrigenId", concesionOrigenId)
                .param("personaDestinoId", personaDestinoId)
                .param("cuotaBaseUma", cuotaBaseUma != null ? cuotaBaseUma : BigDecimal.valueOf(10.00))
                .param("fecha", fecha)
                .query((rs, rowNum) -> new TraspasoCalculoDerechoDto(
                        (UUID) rs.getObject("concesion_origen_id"),
                        (UUID) rs.getObject("local_id"),
                        rs.getLong("mercado_id"),
                        (UUID) rs.getObject("persona_destino_id"),
                        rs.getBoolean("es_familiar_suplente"),
                        rs.getString("parentesco_registrado"),
                        rs.getBigDecimal("porcentaje_descuento"),
                        rs.getBigDecimal("uma_aplicada"),
                        rs.getBigDecimal("cuota_base_uma"),
                        rs.getBigDecimal("monto_bruto"),
                        rs.getBigDecimal("monto_bonificacion"),
                        rs.getBigDecimal("monto_neto_pagar")
                ))
                .single();
    }

    /**
     * Registra un permiso temporal de feria express con cobro en campo invocando
     * gesta.fn_registrar_permiso_feria_express(...).
     */
    public UUID registrarPermisoFeriaExpress(PermisoFeriaExpressDto dto) {
        Double lon = (dto.ubicacion() != null) ? dto.ubicacion().longitude() : null;
        Double lat = (dto.ubicacion() != null) ? dto.ubicacion().latitude() : null;
        String forma = (dto.formaPago() != null ? dto.formaPago() : FormaPago.EFECTIVO).name();
        Timestamp tsCobro = dto.fechaCobro() != null ? Timestamp.from(dto.fechaCobro()) : null;

        return jdbcClient.sql("""
                SELECT gesta.fn_registrar_permiso_feria_express(
                    :localId,
                    :titularPersonaId,
                    :giroId,
                    :fechaInicio,
                    :dias,
                    :pagoId,
                    :dispositivoId,
                    :folio,
                    :montoCobrado,
                    :hashOffline,
                    CAST(:formaPago AS gesta.forma_pago),
                    CASE WHEN :lon IS NOT NULL AND :lat IS NOT NULL
                         THEN public.ST_SetSRID(public.ST_MakePoint(:lon, :lat), 4326)
                         ELSE NULL END,
                    :fechaCobro
                )
                """)
                .param("localId", dto.localId())
                .param("titularPersonaId", dto.titularPersonaId())
                .param("giroId", dto.giroId())
                .param("fechaInicio", dto.fechaInicio())
                .param("dias", dto.dias())
                .param("pagoId", dto.pagoId())
                .param("dispositivoId", dto.dispositivoId())
                .param("folio", dto.folio())
                .param("montoCobrado", dto.montoCobrado())
                .param("hashOffline", dto.hashOffline())
                .param("formaPago", forma)
                .param("lon", lon)
                .param("lat", lat)
                .param("fechaCobro", tsCobro)
                .query(UUID.class)
                .single();
    }
}
