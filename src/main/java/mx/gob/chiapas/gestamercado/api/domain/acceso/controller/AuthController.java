package mx.gob.chiapas.gestamercado.api.domain.acceso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.ChangePasswordRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.ConfigureMfaRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.LoginRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.LoginResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.RefreshTokenRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.TokenResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación y Sesiones", description = "Endpoints para inicio de sesión Argon2id, rotación de tokens y control de sesiones")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión", description = "Verifica credenciales mediante Argon2id e invoca funciones PL/pgSQL de autenticación segura.")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest servletRequest
    ) {
        String clientIp = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        LoginResponseDto response = authService.login(request, clientIp, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovación de tokens", description = "Valida y rota el token de refresco invocando fn_sesion_validar y fn_sesion_emitir.")
    public ResponseEntity<TokenResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto request,
            HttpServletRequest servletRequest
    ) {
        String clientIp = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        TokenResponseDto response = authService.refreshToken(request, clientIp, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cierre de sesión", description = "Revoca la sesión activa en gesta.sesion_refresh.")
    public ResponseEntity<Void> logout(
            @RequestParam(value = "refreshToken", required = false) String refreshTokenParam,
            @RequestBody(required = false) RefreshTokenRequestDto body
    ) {
        String token = (refreshTokenParam != null && !refreshTokenParam.isBlank())
                ? refreshTokenParam
                : (body != null ? body.refreshToken() : null);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambio de contraseña", description = "Actualiza la contraseña del usuario autenticado o administrado con Argon2id.")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mfa/configurar")
    @Operation(summary = "Configuración MFA", description = "Habilita o deshabilita el segundo factor de autenticación.")
    public ResponseEntity<Void> configureMfa(@Valid @RequestBody ConfigureMfaRequestDto request) {
        authService.configureMfa(request);
        return ResponseEntity.noContent().build();
    }
}
