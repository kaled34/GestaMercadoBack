package mx.gob.chiapas.gestamercado.api.domain.acceso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.RolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioRolAssignDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioRolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios y Roles", description = "Endpoints para administración de cuentas de usuario, asignación de roles y alcances")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario con contraseña cifrada y rol inicial opcional.")
    public ResponseEntity<UsuarioResponseDto> crearUsuario(@Valid @RequestBody UsuarioCreateDto dto) {
        UsuarioResponseDto creado = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TESORERIA') or #id == authentication.principal.id")
    @Operation(summary = "Obtener usuario por ID", description = "Recupera los datos públicos y roles del usuario.")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TESORERIA')")
    @Operation(summary = "Listar usuarios", description = "Retorna una página de usuarios con filtro opcional de estado activo.")
    public ResponseEntity<Page<UsuarioResponseDto>> listar(
            @RequestParam(value = "activo", required = false) Boolean activo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioService.listar(pageable, activo));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar estado de usuario", description = "Modifica el estado activo o la persona vinculada.")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UsuarioUpdateDto dto
    ) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Asignar rol a usuario", description = "Asigna un rol con alcance de mercado o municipio validando exclusiones.")
    public ResponseEntity<UsuarioRolResponseDto> asignarRol(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UsuarioRolAssignDto dto
    ) {
        UsuarioRolResponseDto asignado = usuarioService.asignarRol(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(asignado);
    }

    @DeleteMapping("/{id}/roles/{usuarioRolId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desactivar rol de usuario", description = "Desactiva la asignación de rol indicada.")
    public ResponseEntity<Void> removerRol(
            @PathVariable("id") UUID id,
            @PathVariable("usuarioRolId") UUID usuarioRolId
    ) {
        usuarioService.removerRol(id, usuarioRolId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @Operation(summary = "Catálogo de roles", description = "Lista todos los roles de sistema disponibles en gesta.rol.")
    public ResponseEntity<List<RolResponseDto>> listarRoles() {
        return ResponseEntity.ok(usuarioService.listarRoles());
    }
}
