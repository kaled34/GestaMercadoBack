package mx.gob.chiapas.gestamercado.api.domain.acceso.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.common.exception.UnauthorizedException;
import mx.gob.chiapas.gestamercado.api.config.RlsTransactionAspect;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.ChangePasswordRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.ConfigureMfaRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.LoginRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.LoginResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.RefreshTokenRequestDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.TokenResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioRolEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.AuthJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.UsuarioJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.JwtTokenProvider;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthJdbcRepository authJdbcRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RlsTransactionAspect rlsTransactionAspect;

    public AuthService(AuthJdbcRepository authJdbcRepository,
                       UsuarioJpaRepository usuarioJpaRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder,
                       RlsTransactionAspect rlsTransactionAspect) {
        this.authJdbcRepository = authJdbcRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.rlsTransactionAspect = rlsTransactionAspect;
    }

    private static final String BOOTSTRAP_PREFIX = "{BOOTSTRAP}";

    @Transactional(noRollbackFor = {BadCredentialsException.class, UnauthorizedException.class})
    public LoginResponseDto login(LoginRequestDto request, String clientIp, String userAgent) {
        // 1. Obtención segura de credenciales e intentos fallidos vía PL/pgSQL
        var loginDataOpt = authJdbcRepository.loginObtener(request.username());

        if (loginDataOpt.isEmpty()) {
            log.warn("Intento de login fallido: usuario '{}' no existe", request.username());
            throw new BadCredentialsException("Credenciales de acceso incorrectas");
        }

        var loginData = loginDataOpt.get();

        if (!loginData.activo()) {
            throw new UnauthorizedException("La cuenta de usuario se encuentra inactiva");
        }

        if (loginData.bloqueadoHasta() != null && loginData.bloqueadoHasta().isAfter(Instant.now())) {
            throw new UnauthorizedException("La cuenta está bloqueada temporalmente por exceso de intentos fallidos hasta "
                    + loginData.bloqueadoHasta());
        }

        // 2. Validación de contraseña mediante Argon2id (con soporte de migración {BOOTSTRAP} en primer acceso)
        String storedHash = loginData.passwordHash();
        boolean isBootstrap = storedHash != null && storedHash.startsWith(BOOTSTRAP_PREFIX);
        boolean matches;
        if (isBootstrap) {
            String expectedPlain = storedHash.substring(BOOTSTRAP_PREFIX.length());
            matches = expectedPlain.equals(request.password());
        } else {
            matches = storedHash != null && passwordEncoder.matches(request.password(), storedHash);
        }

        if (!matches) {
            // Registro de fallo con incremento de intentos y eventual bloqueo por 15 minutos (CU-01 / CU-02)
            authJdbcRepository.loginResultado(loginData.id(), false, 5, "15 minutes");
            log.warn("Contraseña incorrecta para usuario '{}'. Fallos registrados.", request.username());
            throw new BadCredentialsException("Credenciales de acceso incorrectas");
        }

        // 3. Registro de éxito (resetea intentos_fallidos a 0 y actualiza ultimo_acceso)
        authJdbcRepository.loginResultado(loginData.id(), true, 5, "15 minutes");

        // 4. Inyección de identidad RLS con firma HMAC para consultar gesta.usuario y gesta.usuario_rol
        rlsTransactionAspect.injectUserContext(loginData.id());

        // Si la cuenta tenía semilla {BOOTSTRAP}, se cifra inmediatamente con Argon2id y se persiste en BD
        if (isBootstrap) {
            String argon2Hash = passwordEncoder.encode(request.password());
            authJdbcRepository.usuarioSetPassword(loginData.id(), argon2Hash);
            log.info("Contraseña semilla migrada automáticamente a hash Argon2id para usuario '{}'", request.username());
        }

        // 5. Consulta de roles y alcances activos del usuario
        UsuarioEntity usuarioEntity = usuarioJpaRepository.findByIdWithRoles(loginData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", loginData.id()));

        List<String> roles = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(r -> r.getRol().getClave())
                .distinct()
                .toList();

        Long municipioId = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(UsuarioRolEntity::getMunicipioId)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        Long mercadoId = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(UsuarioRolEntity::getMercadoId)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        // 5. Generación de Access Token JWT
        String accessToken = jwtTokenProvider.generateAccessToken(
                loginData.id(),
                usuarioEntity.getUsername(),
                roles,
                municipioId,
                mercadoId
        );

        // 6. Generación y persistencia de Refresh Token (huella SHA-256 en sesion_refresh)
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken();
        String tokenHash = jwtTokenProvider.hashToken(rawRefreshToken);
        Instant refreshExpiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs());

        authJdbcRepository.sesionEmitir(
                loginData.id(),
                request.dispositivoId(),
                tokenHash,
                refreshExpiry,
                clientIp,
                userAgent
        );

        log.info("Sesión iniciada con éxito para usuario '{}' (ID: {})", usuarioEntity.getUsername(), loginData.id());

        return new LoginResponseDto(
                accessToken,
                rawRefreshToken,
                jwtTokenProvider.getAccessTokenExpirationMs(),
                loginData.id(),
                usuarioEntity.getUsername(),
                loginData.debeCambiarPass(),
                loginData.mfaHabilitado(),
                roles,
                municipioId,
                mercadoId
        );
    }

    @Transactional
    public TokenResponseDto refreshToken(RefreshTokenRequestDto request, String clientIp, String userAgent) {
        String tokenHash = jwtTokenProvider.hashToken(request.refreshToken());

        // Valida contra gesta.sesion_refresh vía función de base de datos
        var sesionOpt = authJdbcRepository.sesionValidar(tokenHash);
        if (sesionOpt.isEmpty()) {
            throw new UnauthorizedException("El token de refresco es inválido, ha expirado o fue revocado");
        }

        var sesion = sesionOpt.get();

        // Rotación obligatoria de token: revocar el anterior para evitar reutilización
        authJdbcRepository.sesionRevocar(tokenHash);

        // Inyección de identidad RLS con firma HMAC para consultar gesta.usuario y gesta.usuario_rol
        rlsTransactionAspect.injectUserContext(sesion.usuarioId());

        UsuarioEntity usuarioEntity = usuarioJpaRepository.findByIdWithRoles(sesion.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", sesion.usuarioId()));

        if (!usuarioEntity.isActivo()) {
            throw new UnauthorizedException("La cuenta de usuario se encuentra inactiva");
        }

        List<String> roles = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(r -> r.getRol().getClave())
                .distinct()
                .toList();

        Long municipioId = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(UsuarioRolEntity::getMunicipioId)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        Long mercadoId = usuarioEntity.getRoles().stream()
                .filter(UsuarioRolEntity::isActivo)
                .map(UsuarioRolEntity::getMercadoId)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                usuarioEntity.getId(),
                usuarioEntity.getUsername(),
                roles,
                municipioId,
                mercadoId
        );

        String newRawRefreshToken = jwtTokenProvider.generateRefreshToken();
        String newTokenHash = jwtTokenProvider.hashToken(newRawRefreshToken);
        Instant newExpiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs());

        authJdbcRepository.sesionEmitir(
                usuarioEntity.getId(),
                sesion.dispositivoId(),
                newTokenHash,
                newExpiry,
                clientIp,
                userAgent
        );

        return new TokenResponseDto(
                newAccessToken,
                newRawRefreshToken,
                jwtTokenProvider.getAccessTokenExpirationMs()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            String tokenHash = jwtTokenProvider.hashToken(refreshToken);
            authJdbcRepository.sesionRevocar(tokenHash);
            log.info("Sesión refresh revocada exitosamente");
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Operación no autorizada"));

        UUID targetUserId = (request.usuarioId() != null) ? request.usuarioId() : currentUserId;

        // Si es cambio sobre su propia cuenta, verificar la contraseña actual
        if (currentUserId.equals(targetUserId)) {
            if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                throw new BusinessRuleException("Debe proporcionar la contraseña actual para autogestión");
            }
            String currentUsername = SecurityUtils.getCurrentUsername().orElse("");
            var loginData = authJdbcRepository.loginObtener(currentUsername)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", targetUserId));

            if (!passwordEncoder.matches(request.currentPassword(), loginData.passwordHash())) {
                throw new BadCredentialsException("La contraseña actual no coincide");
            }
        }

        // Cifrado con Argon2id y delegación a gesta.fn_usuario_set_password
        String newHash = passwordEncoder.encode(request.newPassword());
        authJdbcRepository.usuarioSetPassword(targetUserId, newHash);
        log.info("Contraseña actualizada con éxito para el usuario {}", targetUserId);
    }

    @Transactional
    public void configureMfa(ConfigureMfaRequestDto request) {
        byte[] secretoBytes = null;
        if (request.habilitado()) {
            if (request.secretoCifradoBase64() == null || request.secretoCifradoBase64().isBlank()) {
                throw new BusinessRuleException("Se requiere el secreto MFA cifrado para habilitar el segundo factor");
            }
            try {
                secretoBytes = Base64.getDecoder().decode(request.secretoCifradoBase64());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("El secreto MFA cifrado debe estar codificado en Base64 válido");
            }
        }

        authJdbcRepository.usuarioSetMfa(request.habilitado(), secretoBytes);
        log.info("Configuración de MFA actualizada: habilitado={}", request.habilitado());
    }
}
