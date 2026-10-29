package mx.gob.chiapas.gestamercado.api.domain.titularidad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoAprobarDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCalculoDerechoDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.service.TraspasoService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/traspasos")
@Tag(name = "Titularidad - Traspasos", description = "Trámites de traspasos formales, informales notificados y liquidación de derechos")
public class TraspasoController {

    private final TraspasoService traspasoService;

    public TraspasoController(TraspasoService traspasoService) {
        this.traspasoService = traspasoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Iniciar solicitud o notificación de traspaso de concesión")
    public ResponseEntity<TraspasoDto> iniciar(@Valid @RequestBody TraspasoCreateDto dto) {
        TraspasoDto creado = traspasoService.iniciarTraspaso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener detalle del trámite de traspaso por ID")
    public ResponseEntity<TraspasoDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(traspasoService.obtenerPorId(id));
    }

    @GetMapping("/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar traspasos asociados a un local comercial")
    public ResponseEntity<List<TraspasoDto>> listarPorLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(traspasoService.listarPorLocal(localId));
    }

    @GetMapping("/concesion/{concesionId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar trámites de traspaso derivados de una concesión de origen")
    public ResponseEntity<List<TraspasoDto>> listarPorConcesion(@PathVariable UUID concesionId) {
        return ResponseEntity.ok(traspasoService.listarPorConcesion(concesionId));
    }

    @GetMapping("/calcular-derecho")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Calcular cuota de derecho de traspaso conforme a UMA y descuento por suplente familiar (invoca gesta.fn_calcular_derecho_traspaso)")
    public ResponseEntity<TraspasoCalculoDerechoDto> calcularDerecho(
            @RequestParam UUID concesionOrigenId,
            @RequestParam UUID personaDestinoId,
            @RequestParam(required = false, defaultValue = "10.0") BigDecimal cuotaBaseUma,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(traspasoService.calcularDerecho(concesionOrigenId, personaDestinoId, cuotaBaseUma, fecha));
    }

    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Aprobar y dictaminar formalmente un traspaso (genera nueva concesión vía trigger PostGIS/PostgreSQL)")
    public ResponseEntity<TraspasoDto> dictaminarAprobacion(
            @PathVariable UUID id,
            @Valid @RequestBody TraspasoAprobarDto dto) {
        return ResponseEntity.ok(traspasoService.dictaminarAprobacion(id, dto));
    }

    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Rechazar solicitud de traspaso")
    public ResponseEntity<TraspasoDto> dictaminarRechazo(
            @PathVariable UUID id,
            @RequestParam(required = false) String notas) {
        return ResponseEntity.ok(traspasoService.dictaminarRechazo(id, notas));
    }
}
