package mx.gob.chiapas.gestamercado.api.domain.cobranza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCancelarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCobroCampoDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDetalleDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoRevisionDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.service.CobranzaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cobranza")
@Tag(name = "Cobranza - Operaciones en Campo y Ventanilla", description = "Sincronización offline, cobro móvil con PostGIS, ventanilla fiscal y amortizaciones")
public class CobranzaController {

    private final CobranzaService cobranzaService;

    public CobranzaController(CobranzaService cobranzaService) {
        this.cobranzaService = cobranzaService;
    }

    @PostMapping("/campo")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Registrar cobro en campo con firma criptográfica offline y georreferenciación (invoca gesta.fn_registrar_cobro_campo)")
    public ResponseEntity<PagoDto> registrarCobroCampo(@Valid @RequestBody PagoCobroCampoDto dto) {
        PagoDto creado = cobranzaService.registrarCobroCampo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/caja")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Registrar cobro en ventanilla fiscal / caja (invoca gesta.fn_registrar_pago_caja)")
    public ResponseEntity<PagoDto> registrarPagoCaja(@Valid @RequestBody PagoCajaDto dto) {
        PagoDto creado = cobranzaService.registrarPagoCaja(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/{pagoId}/aplicar/{cargoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Aplicar o amortizar manualmente un pago contra un cargo específico (invoca gesta.fn_aplicar_pago)")
    public ResponseEntity<String> aplicarPago(
            @PathVariable UUID pagoId,
            @PathVariable UUID cargoId,
            @RequestParam(required = false) BigDecimal monto) {
        String resultado = cobranzaService.aplicarPagoACargo(pagoId, cargoId, monto);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/saldo-favor/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Aplicar saldo a favor acumulado a los cargos pendientes del local (invoca gesta.fn_aplicar_saldo_favor_local)")
    public ResponseEntity<BigDecimal> aplicarSaldoFavor(
            @PathVariable UUID localId,
            @RequestParam(required = false) UUID concesionId) {
        BigDecimal aplicado = cobranzaService.aplicarSaldoFavor(localId, concesionId);
        return ResponseEntity.ok(aplicado);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Cancelar pago con justificación de auditoría (invoca gesta.fn_cancelar_pago)")
    public ResponseEntity<PagoDto> cancelarPago(
            @PathVariable UUID id,
            @Valid @RequestBody PagoCancelarDto dto) {
        return ResponseEntity.ok(cobranzaService.cancelarPago(id, dto.motivo()));
    }

    @PostMapping("/{id}/resolver-revision")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Resolver revisión de pago observado (invoca gesta.fn_resolver_revision_pago)")
    public ResponseEntity<PagoDto> resolverRevision(
            @PathVariable UUID id,
            @Valid @RequestBody PagoRevisionDto dto) {
        return ResponseEntity.ok(cobranzaService.resolverRevision(id, dto.motivo()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar pago por ID")
    public ResponseEntity<PagoDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cobranzaService.obtenerPorId(id));
    }

    @GetMapping("/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar pagos efectuados por un local comercial")
    public ResponseEntity<List<PagoDto>> listarPorLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(cobranzaService.listarPorLocal(localId));
    }

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar pagos registrados en un mercado")
    public ResponseEntity<List<PagoDto>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(cobranzaService.listarPorMercado(mercadoId));
    }

    @GetMapping("/cobrador/{cobradorId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar pagos recaudados por un cobrador")
    public ResponseEntity<List<PagoDto>> listarPorCobrador(@PathVariable UUID cobradorId) {
        return ResponseEntity.ok(cobranzaService.listarPorCobrador(cobradorId));
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar desglose de cargos amortizados por un pago")
    public ResponseEntity<List<PagoDetalleDto>> listarDetalles(@PathVariable UUID id) {
        return ResponseEntity.ok(cobranzaService.listarDetalles(id));
    }
}
