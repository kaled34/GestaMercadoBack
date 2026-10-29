package mx.gob.chiapas.gestamercado.api.domain.acceso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.service.DispositivoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dispositivos")
@Tag(name = "Dispositivos Móviles", description = "Endpoints para enrolamiento y gestión de terminales móviles de cobro en campo")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Enrolar nuevo dispositivo", description = "Registra un dispositivo móvil con clave secreta HMAC-SHA256 y serie única.")
    public ResponseEntity<DispositivoResponseDto> crear(@Valid @RequestBody DispositivoCreateDto dto) {
        DispositivoResponseDto creado = dispositivoService.crearDispositivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TESORERIA', 'INSPECTOR')")
    @Operation(summary = "Obtener dispositivo por ID", description = "Consulta los datos de un dispositivo por su UUID.")
    public ResponseEntity<DispositivoResponseDto> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dispositivoService.obtenerPorId(id));
    }

    @GetMapping("/serie/{serieFolio}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TESORERIA', 'INSPECTOR')")
    @Operation(summary = "Obtener dispositivo por serie", description = "Consulta un dispositivo a partir de su prefijo de serie de folios.")
    public ResponseEntity<DispositivoResponseDto> obtenerPorSerie(@PathVariable("serieFolio") String serieFolio) {
        return ResponseEntity.ok(dispositivoService.obtenerPorSerie(serieFolio));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TESORERIA')")
    @Operation(summary = "Listar dispositivos", description = "Retorna una página de terminales con filtros de mercado y actividad.")
    public ResponseEntity<Page<DispositivoResponseDto>> listar(
            @RequestParam(value = "mercadoId", required = false) Long mercadoId,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(dispositivoService.listar(mercadoId, activo, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar dispositivo", description = "Modifica el alias, modelo o estado de actividad.")
    public ResponseEntity<DispositivoResponseDto> actualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody DispositivoUpdateDto dto
    ) {
        return ResponseEntity.ok(dispositivoService.actualizarDispositivo(id, dto));
    }

    @PostMapping("/{id}/rotar-clave")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Rotar clave simétrica HMAC", description = "Genera una nueva clave simétrica de 256 bits para el dispositivo.")
    public ResponseEntity<DispositivoResponseDto> rotarClave(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dispositivoService.rotarClaveHmac(id));
    }
}
