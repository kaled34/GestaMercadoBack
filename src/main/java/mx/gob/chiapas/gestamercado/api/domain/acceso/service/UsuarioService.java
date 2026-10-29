package mx.gob.chiapas.gestamercado.api.domain.acceso.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.RolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioRolAssignDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioRolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.mapper.RolMapper;
import mx.gob.chiapas.gestamercado.api.domain.acceso.mapper.UsuarioMapper;
import mx.gob.chiapas.gestamercado.api.domain.acceso.mapper.UsuarioRolMapper;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.RolEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioRolEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.AuthJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.RolJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.UsuarioJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.UsuarioRolJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final RolJpaRepository rolJpaRepository;
    private final UsuarioRolJpaRepository usuarioRolJpaRepository;
    private final AuthJdbcRepository authJdbcRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final UsuarioRolMapper usuarioRolMapper;

    public UsuarioService(UsuarioJpaRepository usuarioJpaRepository,
                          RolJpaRepository rolJpaRepository,
                          UsuarioRolJpaRepository usuarioRolJpaRepository,
                          AuthJdbcRepository authJdbcRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper,
                          RolMapper rolMapper,
                          UsuarioRolMapper usuarioRolMapper) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.rolJpaRepository = rolJpaRepository;
        this.usuarioRolJpaRepository = usuarioRolJpaRepository;
        this.authJdbcRepository = authJdbcRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.rolMapper = rolMapper;
        this.usuarioRolMapper = usuarioRolMapper;
    }

    @Transactional
    public UsuarioResponseDto crearUsuario(UsuarioCreateDto dto) {
        if (usuarioJpaRepository.existsByUsernameIgnoreCase(dto.username().trim())) {
            throw new ConflictException("El nombre de usuario '" + dto.username() + "' ya se encuentra registrado");
        }

        UUID creadorId = SecurityUtils.getCurrentUserId().orElse(null);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(dto.username().trim());
        usuario.setPersonaId(dto.personaId());
        usuario.setActivo(true);
        usuario.setDebeCambiarPass(true);
        usuario.setCreadoPor(creadorId);

        usuario = usuarioJpaRepository.save(usuario);

        // Encriptar contraseña inicial con Argon2id y asignar vía función SQL
        String passwordHash = passwordEncoder.encode(dto.password());
        authJdbcRepository.usuarioSetPassword(usuario.getId(), passwordHash);

        // Asignación de rol inicial si fue indicado
        if (dto.rolInicialId() != null) {
            RolEntity rol = rolJpaRepository.findById(dto.rolInicialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.rolInicialId()));

            validarAlcanceRol(rol, dto.municipioId(), dto.mercadoId());

            UsuarioRolEntity asignacion = new UsuarioRolEntity(
                    usuario,
                    rol,
                    dto.municipioId(),
                    dto.mercadoId(),
                    true
            );
            usuario.addRol(asignacion);
            usuarioRolJpaRepository.save(asignacion);
        }

        log.info("Usuario '{}' creado con éxito con ID: {}", usuario.getUsername(), usuario.getId());
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPorId(UUID id) {
        UsuarioEntity usuario = usuarioJpaRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> listar(Pageable pageable, Boolean activo) {
        Page<UsuarioEntity> pagina = (activo != null)
                ? usuarioJpaRepository.findByActivo(activo, pageable)
                : usuarioJpaRepository.findAll(pageable);
        return pagina.map(usuarioMapper::toDto);
    }

    @Transactional
    public UsuarioResponseDto actualizarUsuario(UUID id, UsuarioUpdateDto dto) {
        UsuarioEntity usuario = usuarioJpaRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (dto.activo() != null) {
            usuario.setActivo(dto.activo());
        }
        if (dto.personaId() != null) {
            usuario.setPersonaId(dto.personaId());
        }

        usuario = usuarioJpaRepository.save(usuario);
        log.info("Usuario '{}' actualizado", usuario.getUsername());
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public UsuarioRolResponseDto asignarRol(UUID usuarioId, UsuarioRolAssignDto dto) {
        UsuarioEntity usuario = usuarioJpaRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        RolEntity rol = rolJpaRepository.findById(dto.rolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.rolId()));

        validarAlcanceRol(rol, dto.municipioId(), dto.mercadoId());

        if (dto.mercadoId() != null && usuarioRolJpaRepository.existsByUsuarioIdAndRolIdAndMercadoIdAndActivoTrue(usuarioId, dto.rolId(), dto.mercadoId())) {
            throw new ConflictException("El usuario ya tiene asignado el rol " + rol.getClave() + " en el mercado " + dto.mercadoId());
        }
        if (dto.municipioId() != null && usuarioRolJpaRepository.existsByUsuarioIdAndRolIdAndMunicipioIdAndActivoTrue(usuarioId, dto.rolId(), dto.municipioId())) {
            throw new ConflictException("El usuario ya tiene asignado el rol " + rol.getClave() + " en el municipio " + dto.municipioId());
        }

        UsuarioRolEntity asignacion = new UsuarioRolEntity(
                usuario,
                rol,
                dto.municipioId(),
                dto.mercadoId(),
                true
        );
        asignacion = usuarioRolJpaRepository.save(asignacion);
        usuario.addRol(asignacion);

        log.info("Rol '{}' asignado a usuario '{}'", rol.getClave(), usuario.getUsername());
        return usuarioRolMapper.toDto(asignacion);
    }

    @Transactional
    public void removerRol(UUID usuarioId, UUID usuarioRolId) {
        UsuarioRolEntity asignacion = usuarioRolJpaRepository.findById(usuarioRolId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rol", usuarioRolId));

        if (!asignacion.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessRuleException("La asignación de rol no corresponde al usuario indicado");
        }

        asignacion.setActivo(false);
        usuarioRolJpaRepository.save(asignacion);
        log.info("Asignación de rol {} desactivada para usuario {}", usuarioRolId, usuarioId);
    }

    @Transactional(readOnly = true)
    public List<RolResponseDto> listarRoles() {
        return rolMapper.toDtoList(rolJpaRepository.findAll());
    }

    private void validarAlcanceRol(RolEntity rol, Long municipioId, Long mercadoId) {
        // Validación de restricción CHECK ck_usuario_rol_un_alcance: num_nonnulls(municipio_id, mercado_id) <= 1
        if (municipioId != null && mercadoId != null) {
            throw new BusinessRuleException("Un rol no puede tener simultáneamente alcance municipal y de mercado");
        }

        if (rol.isRequiereAlcance() && municipioId == null && mercadoId == null) {
            throw new BusinessRuleException("El rol '" + rol.getClave() + "' requiere obligatoriamente un alcance (mercado o municipio)");
        }
    }
}
