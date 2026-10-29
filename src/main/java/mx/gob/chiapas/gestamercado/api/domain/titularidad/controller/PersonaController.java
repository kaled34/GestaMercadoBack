package mx.gob.chiapas.gestamercado.api.domain.titularidad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaRefDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.service.PersonaService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/personas")
@Tag(name = "Titularidad - Personas", description = "Gestión del padrón de locatarios, comerciantes y personas físicas/morales")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Registrar nueva persona (física o moral) en el padrón")
    public ResponseEntity<PersonaDto> crear(@Valid @RequestBody PersonaCreateDto dto) {
        PersonaDto creada = personaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Actualizar datos generales de una persona")
    public ResponseEntity<PersonaDto> actualizar(@PathVariable UUID id, @Valid @RequestBody PersonaUpdateDto dto) {
        return ResponseEntity.ok(personaService.actualizar(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Consultar persona por ID")
    public ResponseEntity<PersonaDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(personaService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO', 'COBRADOR', 'CONSULTA')")
    @Operation(summary = "Buscar personas por término (nombre, razón social, CURP, RFC)")
    public ResponseEntity<List<PersonaDto>> buscar(@RequestParam(required = false) String termino) {
        return ResponseEntity.ok(personaService.buscar(termino));
    }

    @PostMapping("/buscar-vincular")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_MERCADO')")
    @Operation(summary = "Buscar persona por CURP o RFC y vincularla al mercado (invoca gesta.fn_buscar_vincular_persona)")
    public ResponseEntity<PersonaRefDto> buscarYVincular(
            @RequestParam(required = false) String curp,
            @RequestParam(required = false) String rfc,
            @RequestParam Long mercadoId) {
        return personaService.buscarYVincular(curp, rfc, mercadoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
