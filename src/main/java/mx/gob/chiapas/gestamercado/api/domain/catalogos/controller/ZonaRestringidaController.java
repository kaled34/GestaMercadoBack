package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.ZonaRestringidaService;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoZonaRest;
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

@RestController
@RequestMapping("/api/v1/zonas-restringidas")
@Tag(name = "Zonas Restringidas", description = "Endpoints para perímetros de restricción espacial (Primer Cuadro, Escuelas, Hospitales, Templos)")
public class ZonaRestringidaController {

    private final ZonaRestringidaService zonaRestringidaService;

    public ZonaRestringidaController(ZonaRestringidaService zonaRestringidaService) {
        this.zonaRestringidaService = zonaRestringidaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear zona restringida", description = "Registra un polígono PostGIS protegido donde se valida la prohibición de comercio fijo.")
    public ResponseEntity<ZonaRestringidaDto> crear(@Valid @RequestBody ZonaRestringidaCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zonaRestringidaService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener zona restringida por ID")
    public ResponseEntity<ZonaRestringidaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(zonaRestringidaService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar zonas restringidas de un municipio", description = "Retorna los polígonos protegidos filtrando por tipo.")
    public ResponseEntity<List<ZonaRestringidaDto>> listarPorMunicipio(
            @RequestParam("municipioId") Long municipioId,
            @RequestParam(value = "tipo", required = false) TipoZonaRest tipo
    ) {
        return ResponseEntity.ok(zonaRestringidaService.listarPorMunicipio(municipioId, tipo));
    }
}
