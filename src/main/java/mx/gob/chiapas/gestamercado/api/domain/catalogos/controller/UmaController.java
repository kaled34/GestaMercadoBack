package mx.gob.chiapas.gestamercado.api.domain.catalogos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaMercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaOficialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.service.UmaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/uma")
@Tag(name = "Valores de UMA", description = "Endpoints para consulta y captura de valores oficiales y específicos de UMA por mercado")
public class UmaController {

    private final UmaService umaService;

    public UmaController(UmaService umaService) {
        this.umaService = umaService;
    }

    @PostMapping("/mercado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar UMA por mercado", description = "Captura el valor de UMA aplicable a un mercado iniciando el 1 de febrero.")
    public ResponseEntity<UmaMercadoDto> registrarUmaMercado(@Valid @RequestBody UmaMercadoCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(umaService.registrarUmaMercado(dto));
    }

    @GetMapping("/mercado")
    @Operation(summary = "Listar valores UMA de un mercado")
    public ResponseEntity<List<UmaMercadoDto>> listarPorMercado(@RequestParam("mercadoId") Long mercadoId) {
        return ResponseEntity.ok(umaService.listarPorMercado(mercadoId));
    }

    @GetMapping("/oficiales")
    @Operation(summary = "Listar valores UMA oficiales nacionales (INEGI)")
    public ResponseEntity<List<UmaOficialDto>> listarOficiales() {
        return ResponseEntity.ok(umaService.listarOficiales());
    }

    @GetMapping({"/valor-aplicable", "/vigente"})
    @Operation(summary = "Calcular valor UMA aplicable", description = "Invoca gesta.fn_valor_uma para determinar el valor diario exacto.")
    public ResponseEntity<BigDecimal> obtenerValorUma(
            @RequestParam("mercadoId") Long mercadoId,
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(umaService.obtenerValorUma(fecha, mercadoId));
    }

    @GetMapping("/sugerida")
    @Operation(summary = "Obtener UMA sugerida", description = "Invoca gesta.fn_uma_sugerida para resolver el valor nacional aplicable.")
    public ResponseEntity<BigDecimal> obtenerUmaSugerida(
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(umaService.obtenerUmaSugerida(fecha));
    }
}
