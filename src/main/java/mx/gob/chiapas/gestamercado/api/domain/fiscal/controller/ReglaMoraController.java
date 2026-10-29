package mx.gob.chiapas.gestamercado.api.domain.fiscal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.service.ReglaMoraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reglas-mora")
@Tag(name = "Fiscal - Reglas de Mora", description = "Políticas de recargos moratorios, días de gracia y topes máximos")
public class ReglaMoraController {

    private final ReglaMoraService reglaMoraService;

    public ReglaMoraController(ReglaMoraService reglaMoraService) {
        this.reglaMoraService = reglaMoraService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Registrar nueva regla de recargos moratorios")
    public ResponseEntity<ReglaMoraDto> crear(@Valid @RequestBody ReglaMoraCreateDto dto) {
        ReglaMoraDto creada = reglaMoraService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener regla de mora por ID")
    public ResponseEntity<ReglaMoraDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reglaMoraService.obtenerPorId(id));
    }

    @GetMapping("/municipio/{municipioId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar reglas de mora activas por municipio")
    public ResponseEntity<List<ReglaMoraDto>> listarPorMunicipio(@PathVariable Long municipioId) {
        return ResponseEntity.ok(reglaMoraService.listarPorMunicipio(municipioId));
    }

    @GetMapping("/mercado/{mercadoId}/activa")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener regla de mora activa vigente para un mercado")
    public ResponseEntity<ReglaMoraDto> obtenerActivaParaMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(reglaMoraService.obtenerActivaParaMercado(mercadoId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Desactivar regla de mora")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        reglaMoraService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
