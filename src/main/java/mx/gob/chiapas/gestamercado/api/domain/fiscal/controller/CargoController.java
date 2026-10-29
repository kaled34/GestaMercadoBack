package mx.gob.chiapas.gestamercado.api.domain.fiscal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoAjusteDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoCancelarDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoGenerarLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoGenerarLoteDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CuotaCalculadaDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.service.CargoService;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cargos")
@Tag(name = "Fiscal - Motor de Cargos y Recargos", description = "Generación de derechos de piso, recargos moratorios y ajustes fiscales")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping("/calcular-cuota")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Calcular cuota y tarifa aplicable a un local comercial (invoca gesta.fn_calcular_cuota)")
    public ResponseEntity<CuotaCalculadaDto> calcularCuota(
            @RequestParam UUID localId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(cargoService.calcularCuota(localId, fecha));
    }

    @PostMapping("/generar-local")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Generar cargo individual para un local (invoca gesta.fn_generar_cargo_local)")
    public ResponseEntity<CargoDto> generarCargoLocal(@Valid @RequestBody CargoGenerarLocalDto dto) {
        CargoDto creado = cargoService.generarCargoLocal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/generar-lote")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Emisión masiva de cargos por lote para todo un mercado (invoca gesta.fn_generar_cargos)")
    public ResponseEntity<String> generarCargosLote(@Valid @RequestBody CargoGenerarLoteDto dto) {
        String resultadoJson = cargoService.generarCargosLote(dto);
        return ResponseEntity.ok(resultadoJson);
    }

    @PostMapping("/generar-recargos")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Disparar motor de recargos moratorios de un mercado (invoca gesta.fn_generar_recargos)")
    public ResponseEntity<Integer> generarRecargos(
            @RequestParam Long mercadoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(cargoService.generarRecargos(mercadoId, fecha));
    }

    @PostMapping("/penalizacion-diaria")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Aplicar penalización de morosidad diaria a un local comercial")
    public ResponseEntity<CargoDto> aplicarPenalizacionDiaria(
            @RequestParam UUID localId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(cargoService.aplicarPenalizacionMorosidadDiaria(localId, fecha));
    }

    @PostMapping("/{id}/recalcular")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Recalcular importes de un cargo pendiente con base en la tarifa vigente")
    public ResponseEntity<CargoDto> recalcularCargo(@PathVariable UUID id) {
        return ResponseEntity.ok(cargoService.recalcularCargo(id));
    }

    @PostMapping("/{id}/ajustar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Ajustar monto de un cargo registrando justificación legal")
    public ResponseEntity<CargoDto> ajustarCargo(
            @PathVariable UUID id,
            @Valid @RequestBody CargoAjusteDto dto) {
        return ResponseEntity.ok(cargoService.ajustarCargo(id, dto));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Cancelar cargo con motivo de auditoría")
    public ResponseEntity<CargoDto> cancelarCargo(
            @PathVariable UUID id,
            @Valid @RequestBody CargoCancelarDto dto) {
        return ResponseEntity.ok(cargoService.cancelarCargo(id, dto));
    }

    @PostMapping("/{id}/anular-recargos")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Anular recargos indebidos vinculados a un cargo base")
    public ResponseEntity<Integer> anularRecargos(@PathVariable UUID id) {
        return ResponseEntity.ok(cargoService.anularRecargosIndebidos(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener detalle de un cargo por ID")
    public ResponseEntity<CargoDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cargoService.obtenerPorId(id));
    }

    @GetMapping("/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar cargos asociados a un local comercial")
    public ResponseEntity<List<CargoDto>> listarPorLocal(
            @PathVariable UUID localId,
            @RequestParam(required = false) EstadoCargo estado) {
        if (estado != null) {
            return ResponseEntity.ok(cargoService.listarPorLocalYEstado(localId, estado));
        }
        return ResponseEntity.ok(cargoService.listarPorLocal(localId));
    }
}
