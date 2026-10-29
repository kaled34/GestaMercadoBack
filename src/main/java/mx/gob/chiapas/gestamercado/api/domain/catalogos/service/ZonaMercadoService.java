package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.ZonaMercadoMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaMercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.ZonaMercadoJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZonaMercadoService {

    private static final Logger log = LoggerFactory.getLogger(ZonaMercadoService.class);

    private final ZonaMercadoJpaRepository zonaMercadoJpaRepository;
    private final MercadoJpaRepository mercadoJpaRepository;
    private final ZonaMercadoMapper zonaMercadoMapper;

    public ZonaMercadoService(ZonaMercadoJpaRepository zonaMercadoJpaRepository,
                              MercadoJpaRepository mercadoJpaRepository,
                              ZonaMercadoMapper zonaMercadoMapper) {
        this.zonaMercadoJpaRepository = zonaMercadoJpaRepository;
        this.mercadoJpaRepository = mercadoJpaRepository;
        this.zonaMercadoMapper = zonaMercadoMapper;
    }

    @Transactional
    public ZonaMercadoDto crear(ZonaMercadoCreateDto dto) {
        if (!mercadoJpaRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        String nombre = dto.nombre().trim();
        if (zonaMercadoJpaRepository.existsByMercadoIdAndNombreIgnoreCase(dto.mercadoId(), nombre)) {
            throw new ConflictException("Ya existe una zona llamada '" + nombre + "' en el mercado " + dto.mercadoId());
        }

        ZonaMercadoEntity entity = zonaMercadoMapper.toEntity(dto);
        entity.setNombre(nombre);
        entity = zonaMercadoJpaRepository.save(entity);
        log.info("Zona de mercado '{}' creada en mercado {}", nombre, dto.mercadoId());
        return zonaMercadoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ZonaMercadoDto obtenerPorId(Long id) {
        ZonaMercadoEntity entity = zonaMercadoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona de mercado", id));
        return zonaMercadoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ZonaMercadoDto> listarPorMercado(Long mercadoId) {
        return zonaMercadoMapper.toDtoList(zonaMercadoJpaRepository.findByMercadoId(mercadoId));
    }
}
