package mx.gob.chiapas.gestamercado.api.domain.fiscal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.service.TarifaService;
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
@RequestMapping("/api/v1/tarifas")
@Tag(name = "Fiscal - Tarifas", description = "Catálogo y políticas tarifarias (Ley de Ingresos, Bando Municipal, UMA)")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Configurar nueva regla tarifaria municipal/mercado")
    public ResponseEntity<TarifaDto> crear(@Valid @RequestBody TarifaCreateDto dto) {
        TarifaDto creada = tarifaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Obtener detalle de tarifa por ID")
    public ResponseEntity<TarifaDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tarifaService.obtenerPorId(id));
    }

    @GetMapping("/municipio/{municipioId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar tarifas activas de un municipio")
    public ResponseEntity<List<TarifaDto>> listarPorMunicipio(@PathVariable Long municipioId) {
        return ResponseEntity.ok(tarifaService.listarPorMunicipio(municipioId));
    }

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Listar tarifas activas de un mercado")
    public ResponseEntity<List<TarifaDto>> listarPorMercado(@PathVariable Long mercadoId) {
        return ResponseEntity.ok(tarifaService.listarPorMercado(mercadoId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Desactivar tarifa")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        tarifaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
