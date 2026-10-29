package mx.gob.chiapas.gestamercado.api.domain.cobranza.dto;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonPointDto;
import mx.gob.chiapas.gestamercado.api.domain.enums.CanalCobro;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoPago;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PagoDto(
        UUID id,
        Long mercadoId,
        UUID localId,
        UUID personaPagadorId,
        UUID cobradorId,
        UUID dispositivoId,
        UUID corteCajaId,
        CanalCobro canal,
        String folio,
        String hashOffline,
        BigDecimal monto,
        FormaPago formaPago,
        Instant fechaPago,
        Instant sincronizadoEn,
        GeoJsonPointDto ubicacionCobro,
        EstadoPago estado,
        String motivoCancelacion,
        UUID depositoId,
        Boolean requiereRevision,
        String motivoRevision,
        Instant creadoEn,
        String folioImpreso,
        Long consecutivoDispositivo
) {}
