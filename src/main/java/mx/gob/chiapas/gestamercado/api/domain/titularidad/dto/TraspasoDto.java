package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.ResultadoDictamen;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoTramite;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TraspasoDto(
        UUID id,
        UUID localId,
        UUID concesionOrigenId,
        UUID concesionDestinoId,
        UUID personaOrigenId,
        UUID personaDestinoId,
        TipoTramite tipoTramite,
        LocalDate fechaSolicitud,
        LocalDate fechaNotificacion,
        LocalDate fechaEfectiva,
        String numeroExpediente,
        ResultadoDictamen dictamen,
        LocalDate fechaDictamen,
        LocalDate notificadoAFiscalEn,
        Boolean regularizado,
        UUID registradoPor,
        String notas,
        Instant creadoEn,
        BigDecimal cuotaBaseUma,
        UUID cargoTraspasoId
) {}
