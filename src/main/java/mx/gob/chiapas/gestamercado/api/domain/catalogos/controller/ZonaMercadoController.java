package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.ZonaMercadoService;
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
@RequestMapping("/api/v1/zonas-mercado")
@Tag(name = "Zonas de Mercado", description = "Endpoints para administración de naves, pasillos y sectores internos de mercado")
public class ZonaMercadoController {

    private final ZonaMercadoService zonaMercadoService;

    public ZonaMercadoController(ZonaMercadoService zonaMercadoService) {
        this.zonaMercadoService = zonaMercadoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear zona de mercado", description = "Registra una zona interna con delimitación poligonal PostGIS opcional.")
    public ResponseEntity<ZonaMercadoDto> crear(@Valid @RequestBody ZonaMercadoCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zonaMercadoService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener zona por ID")
    public ResponseEntity<ZonaMercadoDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(zonaMercadoService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar zonas de un mercado")
    public ResponseEntity<List<ZonaMercadoDto>> listarPorMercado(@RequestParam("mercadoId") Long mercadoId) {
        return ResponseEntity.ok(zonaMercadoService.listarPorMercado(mercadoId));
    }
}
