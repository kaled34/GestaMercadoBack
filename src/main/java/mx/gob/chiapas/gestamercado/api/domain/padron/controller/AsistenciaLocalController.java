package mx.gob.chiapas.gestamercado.api.domain.padron.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.service.AsistenciaLocalService;
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
@RequestMapping("/api/v1/asistencias")
@Tag(name = "Padrón - Asistencias e Inspecciones", description = "Control de campo y verificación de locales comerciales")
public class AsistenciaLocalController {

    private final AsistenciaLocalService asistenciaService;

    public AsistenciaLocalController(AsistenciaLocalService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR')")
    @Operation(summary = "Registrar inspección / asistencia de local comercial con coordenadas GPS")
    public ResponseEntity<AsistenciaLocalDto> registrar(@Valid @RequestBody AsistenciaLocalCreateDto dto) {
        AsistenciaLocalDto creada = asistenciaService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener detalle de una inspección por ID")
    public ResponseEntity<AsistenciaLocalDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(asistenciaService.obtenerPorId(id));
    }

    @GetMapping("/local/{localId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar inspecciones de un local comercial por rango de fechas")
    public ResponseEntity<List<AsistenciaLocalDto>> listarPorLocal(
            @PathVariable UUID localId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(asistenciaService.listarPorLocal(localId, inicio, fin));
    }
}
