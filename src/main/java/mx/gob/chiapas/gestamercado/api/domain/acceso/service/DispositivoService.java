package mx.gob.chiapas.gestamercado.api.domain.acceso.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.mapper.DispositivoMapper;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.DispositivoEntity;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.AuthJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.DispositivoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.acceso.repository.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HexFormat;
import java.util.UUID;

@Service
public class DispositivoService {

    private static final Logger log = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoJpaRepository dispositivoJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final AuthJdbcRepository authJdbcRepository;
    private final DispositivoMapper dispositivoMapper;

    public DispositivoService(DispositivoJpaRepository dispositivoJpaRepository,
                              UsuarioJpaRepository usuarioJpaRepository,
                              AuthJdbcRepository authJdbcRepository,
                              DispositivoMapper dispositivoMapper) {
        this.dispositivoJpaRepository = dispositivoJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.authJdbcRepository = authJdbcRepository;
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

        DispositivoEntity dispositivo = new DispositivoEntity();
        dispositivo.setUsuarioId(dto.usuarioId());
        dispositivo.setMercadoId(dto.mercadoId());
        dispositivo.setAlias(dto.alias());
        dispositivo.setModelo(dto.modelo());
        dispositivo.setSerieFolio(serie);
        dispositivo.setActivo(true);
        dispositivo.setUltimoConsecutivo(0L);

        dispositivo = dispositivoJpaRepository.saveAndFlush(dispositivo);

        // Obtener/provisionar clave simétrica HMAC-SHA256 de 256 bits vía función SECURITY DEFINER
        String secretoHex = authJdbcRepository.dispositivoRotarSecretoHmac(dispositivo.getId(), null);
        if (secretoHex != null && !secretoHex.isBlank()) {
            dispositivo.setSecretoHmac(HexFormat.of().parseHex(secretoHex));
        }

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

        dispositivo = dispositivoJpaRepository.saveAndFlush(dispositivo);

        if (dto.activo() != null && dto.activo() != dispositivo.isActivo()) {
            authJdbcRepository.dispositivoSetActivo(id, dto.activo(), "Actualización administrativa vía API");
            dispositivo.setActivo(dto.activo());
        }

        log.info("Dispositivo {} actualizado", id);
        return dispositivoMapper.toDto(dispositivo);
    }

    @Transactional
    public DispositivoResponseDto rotarClaveHmac(UUID id) {
        DispositivoEntity dispositivo = dispositivoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));

        String nuevoSecretoHex = authJdbcRepository.dispositivoRotarSecretoHmac(id, null);
        if (nuevoSecretoHex != null && !nuevoSecretoHex.isBlank()) {
            dispositivo.setSecretoHmac(HexFormat.of().parseHex(nuevoSecretoHex));
        }

        log.info("Clave simétrica HMAC rotada para el dispositivo {}", id);
        return dispositivoMapper.toDto(dispositivo);
    }
}
