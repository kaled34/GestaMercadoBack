package mx.gob.chiapas.gestamercado.api.domain.reportes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AdeudoLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AlertaOperativaDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.EfectivoPendienteDepositoDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.LocalCuotaActualDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.PagoSinCorteDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.SaldoFavorDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.service.ReportesFiscalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reportes-fiscales")
@Tag(name = "Reportes y Auditoría - Vistas Fiscales Consolidadas", description = "Consultas consolidadas sobre vistas security_invoker de PostgreSQL")
public class ReportesFiscalesController {

    private final ReportesFiscalesService reportesService;

    public ReportesFiscalesController(ReportesFiscalesService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/adeudos/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Reporte de adeudos vencidos y saldo neto exigible por mercado (vista gesta.v_adeudos)")
    public ResponseEntity<List<AdeudoLocalDto>> listarAdeudosPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(reportesService.listarAdeudosPorMercado(mercadoId));
    }

    @GetMapping("/adeudos/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar adeudo exigible y cargos vencidos de un local comercial")
    public ResponseEntity<AdeudoLocalDto> obtenerAdeudoLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(reportesService.obtenerAdeudoLocal(localId));
    }

    @GetMapping("/saldo-favor/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar saldo a favor de un local comercial (vista gesta.v_saldo_favor)")
    public ResponseEntity<SaldoFavorDto> obtenerSaldoFavorLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(reportesService.obtenerSaldoFavorLocal(localId));
    }

    @GetMapping("/cuota-actual/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar cuota y tarifa aplicable actualmente a un local (vista gesta.v_local_cuota_actual)")
    public ResponseEntity<LocalCuotaActualDto> obtenerCuotaActualLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(reportesService.obtenerCuotaActualLocal(localId));
    }

    @GetMapping("/efectivo-pendiente-deposito")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'CONSULTA')")
    @Operation(summary = "Consultar recaudación en efectivo pendiente de depositar a hacienda (vista gesta.v_efectivo_pendiente_deposito)")
    public ResponseEntity<List<EfectivoPendienteDepositoDto>> listarEfectivoPendienteDeposito(
            @RequestParam(required = false) Long mercadoId) {
        return ResponseEntity.ok(reportesService.listarEfectivoPendienteDeposito(mercadoId));
    }

    @GetMapping("/pagos-sin-corte")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'CONSULTA')")
    @Operation(summary = "Consultar pagos recaudados sin corte de caja diario (vista gesta.v_pagos_sin_corte)")
    public ResponseEntity<List<PagoSinCorteDto>> listarPagosSinCorte(
            @RequestParam(required = false) Long mercadoId) {
        return ResponseEntity.ok(reportesService.listarPagosSinCorte(mercadoId));
    }

    @GetMapping("/alertas/padron")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'CONSULTA')")
    @Operation(summary = "Auditoría de anomalías en padrón: giros no autorizados, traspasos no formalizados (vista gesta.v_alertas_padron)")
    public ResponseEntity<List<AlertaOperativaDto>> listarAlertasPadron(
            @RequestParam(required = false) Long mercadoId) {
        return ResponseEntity.ok(reportesService.listarAlertasPadron(mercadoId));
    }

    @GetMapping("/alertas/cobro")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'CONSULTA')")
    @Operation(summary = "Auditoría de anomalías tarifarias y de cobro: locales sin m2 o sin metros lineales (vista gesta.v_alertas_cobro)")
    public ResponseEntity<List<AlertaOperativaDto>> listarAlertasCobro(
            @RequestParam(required = false) Long mercadoId) {
        return ResponseEntity.ok(reportesService.listarAlertasCobro(mercadoId));
    }
}
