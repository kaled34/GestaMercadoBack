package mx.gob.chiapas.gestamercado.api.domain.padron.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.PadronImportResultDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.PadronImportRowDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.service.LocalComercialService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locales")
@Tag(name = "Padrón Georreferenciado", description = "Endpoints para administración de locales comerciales con geometrías PostGIS")
public class LocalComercialController {

    private final LocalComercialService localService;

    public LocalComercialController(LocalComercialService localService) {
        this.localService = localService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear local comercial", description = "Registra un local con polígono PostGIS. Triggers calculan área y centroide.")
    public ResponseEntity<LocalComercialDto> crear(@Valid @RequestBody LocalComercialCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener local por ID")
    public ResponseEntity<LocalComercialDto> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(localService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar locales comerciales", description = "Retorna una página de locales filtrando por mercado, zona y estatus.")
    public ResponseEntity<Page<LocalComercialDto>> listar(
            @RequestParam("mercadoId") Long mercadoId,
            @RequestParam(value = "zonaId", required = false) Long zonaId,
            @RequestParam(value = "estatus", required = false) EstatusLocal estatus,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(localService.listar(mercadoId, zonaId, estatus, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar local comercial", description = "Modifica atributos y actualiza el polígono georreferenciado.")
    public ResponseEntity<LocalComercialDto> actualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody LocalComercialUpdateDto dto
    ) {
        return ResponseEntity.ok(localService.actualizar(id, dto));
    }

    @PostMapping("/importar/{mercadoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Importación masiva de padrón", description = "Carga por lote registros de locales comerciales validados.")
    public ResponseEntity<PadronImportResultDto> importarPadron(
            @PathVariable("mercadoId") Long mercadoId,
            @RequestBody List<PadronImportRowDto> filas
    ) {
        return ResponseEntity.ok(localService.importarPadron(mercadoId, filas));
    }
}
