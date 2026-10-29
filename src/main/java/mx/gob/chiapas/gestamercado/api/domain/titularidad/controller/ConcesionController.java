package mx.gob.chiapas.gestamercado.api.domain.titularidad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PermisoFeriaExpressDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.service.ConcesionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concesiones")
@Tag(name = "Titularidad - Concesiones", description = "Asignación, suplencias y permisos de concesiones de locales comerciales")
public class ConcesionController {

    private final ConcesionService concesionService;

    public ConcesionController(ConcesionService concesionService) {
        this.concesionService = concesionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Registrar nueva concesión de local comercial")
    public ResponseEntity<ConcesionDto> crear(@Valid @RequestBody ConcesionCreateDto dto) {
        ConcesionDto creada = concesionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Actualizar estado, giro u observaciones de concesión")
    public ResponseEntity<ConcesionDto> actualizar(@PathVariable UUID id, @Valid @RequestBody ConcesionUpdateDto dto) {
        return ResponseEntity.ok(concesionService.actualizar(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener concesión por ID")
    public ResponseEntity<ConcesionDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(concesionService.obtenerPorId(id));
    }

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar concesiones por mercado")
    public ResponseEntity<List<ConcesionDto>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(concesionService.listarPorMercado(mercadoId));
    }

    @GetMapping("/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar historial de concesiones de un local comercial")
    public ResponseEntity<List<ConcesionDto>> listarPorLocal(@PathVariable UUID localId) {
        return ResponseEntity.ok(concesionService.listarPorLocal(localId));
    }

    @GetMapping("/titular/{titularPersonaId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar concesiones otorgadas a una persona")
    public ResponseEntity<List<ConcesionDto>> listarPorTitular(@PathVariable UUID titularPersonaId) {
        return ResponseEntity.ok(concesionService.listarPorTitular(titularPersonaId));
    }

    @PostMapping("/feria-express")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Expedir permiso temporal de feria express (1 a 30 días con cobro en campo)")
    public ResponseEntity<ConcesionDto> registrarPermisoFeriaExpress(@Valid @RequestBody PermisoFeriaExpressDto dto) {
        ConcesionDto concesion = concesionService.registrarPermisoFeriaExpress(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(concesion);
    }

    @PostMapping("/{id}/suplentes")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Registrar familiar suplente en una concesión")
    public ResponseEntity<ConcesionSuplenteDto> agregarSuplente(
            @PathVariable UUID id,
            @Valid @RequestBody ConcesionSuplenteCreateDto dto) {
        if (!id.equals(dto.concesionId())) {
            dto = new ConcesionSuplenteCreateDto(id, dto.personaId(), dto.parentesco());
        }
        ConcesionSuplenteDto suplente = concesionService.agregarSuplente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(suplente);
    }

    @GetMapping("/{id}/suplentes")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar suplentes registrados en una concesión")
    public ResponseEntity<List<ConcesionSuplenteDto>> listarSuplentes(@PathVariable UUID id) {
        return ResponseEntity.ok(concesionService.listarSuplentes(id));
    }

    @DeleteMapping("/suplentes/{suplenteId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Eliminar un suplente de concesión")
    public ResponseEntity<Void> eliminarSuplente(@PathVariable UUID suplenteId) {
        concesionService.eliminarSuplente(suplenteId);
        return ResponseEntity.noContent().build();
    }
}
