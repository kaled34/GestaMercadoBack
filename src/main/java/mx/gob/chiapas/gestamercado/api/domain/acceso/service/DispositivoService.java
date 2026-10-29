package mx.gob.chiapas.gestamercado.api.domain.acceso.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.mapper.DispositivoMapper;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.DispositivoEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.DispositivoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class DispositivoService {

    private static final Logger log = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoJpaRepository dispositivoJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final DispositivoMapper dispositivoMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public DispositivoService(DispositivoJpaRepository dispositivoJpaRepository,
                              UsuarioJpaRepository usuarioJpaRepository,
                              DispositivoMapper dispositivoMapper) {
        this.dispositivoJpaRepository = dispositivoJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.dispositivoMapper = dispositivoMapper;
    }

    @Transactional
    public DispositivoResponseDto crearDispositivo(DispositivoCreateDto dto) {
        String serie = dto.serieFolio().trim().toUpperCase();

        if (dispositivoJpaRepository.existsBySerieFolio(serie)) {
            throw new ConflictException("La serie de folios '" + serie + "' ya se encuentra asignada a otro dispositivo");
        }

        if (!usuarioJpaRepository.existsById(dto.usuarioId())) {
            throw new ResourceNotFoundException("Usuario", dto.usuarioId());
        }

        // Generar clave secreta HMAC-SHA256 de 32 bytes (256 bits) para firma de cobros offline
        byte[] secretoHmac = new byte[32];
        secureRandom.nextBytes(secretoHmac);

        DispositivoEntity dispositivo = new DispositivoEntity();
        dispositivo.setUsuarioId(dto.usuarioId());
        dispositivo.setMercadoId(dto.mercadoId());
        dispositivo.setAlias(dto.alias());
        dispositivo.setModelo(dto.modelo());
        dispositivo.setSerieFolio(serie);
        dispositivo.setActivo(true);
        dispositivo.setUltimoConsecutivo(0L);
        dispositivo.setSecretoHmac(secretoHmac);

        dispositivo = dispositivoJpaRepository.save(dispositivo);
        log.info("Dispositivo enrolado con serie '{}' y ID {}", serie, dispositivo.getId());
        return dispositivoMapper.toDto(dispositivo);
    }

    @Transactional(readOnly = true)
    public DispositivoResponseDto obtenerPorId(UUID id) {
        DispositivoEntity dispositivo = dispositivoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));
        return dispositivoMapper.toDto(dispositivo);
    }

    @Transactional(readOnly = true)
    public DispositivoResponseDto obtenerPorSerie(String serieFolio) {
        DispositivoEntity dispositivo = dispositivoJpaRepository.findBySerieFolio(serieFolio.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo serie", serieFolio));
        return dispositivoMapper.toDto(dispositivo);
    }

    @Transactional(readOnly = true)
    public Page<DispositivoResponseDto> listar(Long mercadoId, Boolean activo, Pageable pageable) {
        Page<DispositivoEntity> pagina;
        if (mercadoId != null) {
            pagina = dispositivoJpaRepository.findByMercadoId(mercadoId, pageable);
        } else if (activo != null) {
            pagina = dispositivoJpaRepository.findByActivo(activo, pageable);
        } else {
            pagina = dispositivoJpaRepository.findAll(pageable);
        }
        return pagina.map(dispositivoMapper::toDto);
    }

    @Transactional
    public DispositivoResponseDto actualizarDispositivo(UUID id, DispositivoUpdateDto dto) {
        DispositivoEntity dispositivo = dispositivoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));

        if (dto.alias() != null) {
            dispositivo.setAlias(dto.alias());
        }
        if (dto.modelo() != null) {
            dispositivo.setModelo(dto.modelo());
        }
        if (dto.activo() != null) {
            dispositivo.setActivo(dto.activo());
        }

        dispositivo = dispositivoJpaRepository.save(dispositivo);
        log.info("Dispositivo {} actualizado", id);
        return dispositivoMapper.toDto(dispositivo);
    }

    @Transactional
    public DispositivoResponseDto rotarClaveHmac(UUID id) {
        DispositivoEntity dispositivo = dispositivoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));

        byte[] nuevoSecreto = new byte[32];
        secureRandom.nextBytes(nuevoSecreto);
        dispositivo.setSecretoHmac(nuevoSecreto);

        dispositivo = dispositivoJpaRepository.save(dispositivo);
        log.info("Clave simétrica HMAC rotada para el dispositivo {}", id);
        return dispositivoMapper.toDto(dispositivo);
    }
}
