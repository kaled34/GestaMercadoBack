package mx.gob.chiapas.gestamercado.api.domain.cobranza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaCerrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.JornadaSinLiquidarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.service.CorteCajaService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cortes-caja")
@Tag(name = "Cobranza - Cortes Ciegos de Caja", description = "Liquidación de jornada con doble ciego, validación de diferencias y control de apertura")
public class CorteCajaController {

    private final CorteCajaService corteService;

    public CorteCajaController(CorteCajaService corteService) {
        this.corteService = corteService;
    }

    @PostMapping("/cerrar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Ejecutar corte ciego de caja diario declarando el monto recaudado (invoca gesta.fn_cerrar_corte)")
    public ResponseEntity<CorteCajaDto> cerrarCorte(@Valid @RequestBody CorteCajaCerrarDto dto) {
        CorteCajaDto creado = corteService.cerrarCorte(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/jornadas-sin-liquidar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Consultar jornadas de cobro pendientes de liquidar por un cobrador (invoca gesta.fn_jornadas_sin_liquidar)")
    public ResponseEntity<List<JornadaSinLiquidarDto>> listarJornadasSinLiquidar(
            @RequestParam UUID cobradorId,
            @RequestParam Long mercadoId) {
        return ResponseEntity.ok(corteService.listarJornadasSinLiquidar(cobradorId, mercadoId));
    }

    @GetMapping("/puede-abrir-jornada")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Verificar si el cobrador puede iniciar una nueva jornada móvil (invoca gesta.fn_puede_abrir_jornada)")
    public ResponseEntity<Boolean> puedeAbrirJornada(
            @RequestParam UUID cobradorId,
            @RequestParam Long mercadoId) {
        return ResponseEntity.ok(corteService.puedeAbrirJornada(cobradorId, mercadoId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener corte de caja por ID")
    public ResponseEntity<CorteCajaDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(corteService.obtenerPorId(id));
    }

    @GetMapping("/cobrador/{cobradorId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Historial de cortes de caja de un cobrador")
    public ResponseEntity<List<CorteCajaDto>> listarPorCobrador(@PathVariable UUID cobradorId) {
        return ResponseEntity.ok(corteService.listarPorCobrador(cobradorId));
    }

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Historial de cortes de caja de un mercado")
    public ResponseEntity<List<CorteCajaDto>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(corteService.listarPorMercado(mercadoId));
    }
}
