package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.MunicipioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/municipios")
@Tag(name = "Municipios", description = "Endpoints para administración de municipios y configuraciones fiscales territoriales")
public class MunicipioController {

    private final MunicipioService municipioService;

    public MunicipioController(MunicipioService municipioService) {
        this.municipioService = municipioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear municipio", description = "Registra un nuevo municipio con su clave INEGI y límites de cobranza.")
    public ResponseEntity<MunicipioDto> crear(@Valid @RequestBody MunicipioCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(municipioService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener municipio por ID")
    public ResponseEntity<MunicipioDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(municipioService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar municipios", description = "Retorna una página de municipios registrados.")
    public ResponseEntity<Page<MunicipioDto>> listar(
            @RequestParam(value = "activo", required = false) Boolean activo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(municipioService.listar(pageable, activo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar municipio")
    public ResponseEntity<MunicipioDto> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody MunicipioCreateDto dto
    ) {
        return ResponseEntity.ok(municipioService.actualizar(id, dto));
    }
}
