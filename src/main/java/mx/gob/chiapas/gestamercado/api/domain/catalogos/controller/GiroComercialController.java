package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.GiroComercialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RestController
@RequestMapping("/api/v1/giros-comerciales")
@Tag(name = "Giros Comerciales", description = "Endpoints para administración del catálogo de giros comerciales permitidos")
public class GiroComercialController {

    private final GiroComercialService giroComercialService;

    public GiroComercialController(GiroComercialService giroComercialService) {
        this.giroComercialService = giroComercialService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear giro comercial", description = "Registra una nueva categoría de giro de actividad comercial.")
    public ResponseEntity<GiroComercialDto> crear(@Valid @RequestBody GiroComercialCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(giroComercialService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener giro comercial por ID")
    public ResponseEntity<GiroComercialDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(giroComercialService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar giros comerciales", description = "Retorna una página de giros con filtro por actividad.")
    public ResponseEntity<Page<GiroComercialDto>> listar(
            @RequestParam(value = "activo", required = false) Boolean activo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(giroComercialService.listar(pageable, activo));
    }
}
