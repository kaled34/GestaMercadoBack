package mx.gob.chiapas.gestamercado.api.domain.cobranza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaRegistrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.service.DepositoHaciendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/depositos-hacienda")
@Tag(name = "Cobranza - Depósitos a Hacienda / Tesorería", description = "Conciliación de depósitos bancarios asociados a pagos y cortes de caja")
public class DepositoHaciendaController {

    private final DepositoHaciendaService depositoService;

    public DepositoHaciendaController(DepositoHaciendaService depositoService) {
        this.depositoService = depositoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Registrar depósito bancario o a tesorería conciliando pagos (invoca gesta.fn_registrar_deposito_hacienda)")
    public ResponseEntity<DepositoHaciendaDto> registrar(@Valid @RequestBody DepositoHaciendaRegistrarDto dto) {
        DepositoHaciendaDto creado = depositoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar depósito por ID")
    public ResponseEntity<DepositoHaciendaDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(depositoService.obtenerPorId(id));
    }

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar depósitos de hacienda por mercado")
    public ResponseEntity<List<DepositoHaciendaDto>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(depositoService.listarPorMercado(mercadoId));
    }
}
