package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDiaInhabilCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDiaInhabilDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.MercadoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mercados")
@Tag(name = "Mercados Públicos", description = "Endpoints para administración de mercados, perímetros PostGIS y calendario de días inhábiles")
public class MercadoController {

    private final MercadoService mercadoService;

    public MercadoController(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear mercado", description = "Registra un mercado con su delimitación perimetral y modelo de cobro fiscal.")
    public ResponseEntity<MercadoDto> crear(@Valid @RequestBody MercadoCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mercadoService.crear(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mercado por ID")
    public ResponseEntity<MercadoDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mercadoService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar mercados", description = "Retorna una página de mercados con filtros por municipio y estado activo.")
    public ResponseEntity<Page<MercadoDto>> listar(
            @RequestParam(value = "municipioId", required = false) Long municipioId,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(mercadoService.listar(municipioId, activo, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar mercado")
    public ResponseEntity<MercadoDto> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody MercadoUpdateDto dto
    ) {
        return ResponseEntity.ok(mercadoService.actualizar(id, dto));
    }

    @PostMapping("/dias-inhabiles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar día inhábil", description = "Agrega un día inhábil que exenta el cómputo de mora y cuota diaria.")
    public ResponseEntity<MercadoDiaInhabilDto> agregarDiaInhabil(@Valid @RequestBody MercadoDiaInhabilCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mercadoService.agregarDiaInhabil(dto));
    }

    @GetMapping("/{id}/dias-inhabiles")
    @Operation(summary = "Listar días inhábiles", description = "Consulta el calendario de días no laborables para un mercado y rango de fechas.")
    public ResponseEntity<List<MercadoDiaInhabilDto>> listarDiasInhabiles(
            @PathVariable("id") Long mercadoId,
            @RequestParam(value = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(value = "fin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return ResponseEntity.ok(mercadoService.listarDiasInhabiles(mercadoId, inicio, fin));
    }
}
