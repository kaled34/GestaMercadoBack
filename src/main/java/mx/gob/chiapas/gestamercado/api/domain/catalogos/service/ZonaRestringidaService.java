package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.ZonaRestringidaMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaRestringidaEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MunicipioJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.ZonaRestringidaJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoZonaRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZonaRestringidaService {

    private static final Logger log = LoggerFactory.getLogger(ZonaRestringidaService.class);

    private final ZonaRestringidaJpaRepository zonaRestringidaJpaRepository;
    private final MunicipioJpaRepository municipioJpaRepository;
    private final ZonaRestringidaMapper zonaRestringidaMapper;

    public ZonaRestringidaService(ZonaRestringidaJpaRepository zonaRestringidaJpaRepository,
                                  MunicipioJpaRepository municipioJpaRepository,
                                  ZonaRestringidaMapper zonaRestringidaMapper) {
        this.zonaRestringidaJpaRepository = zonaRestringidaJpaRepository;
        this.municipioJpaRepository = municipioJpaRepository;
        this.zonaRestringidaMapper = zonaRestringidaMapper;
    }

    @Transactional
    public ZonaRestringidaDto crear(ZonaRestringidaCreateDto dto) {
        if (!municipioJpaRepository.existsById(dto.municipioId())) {
            throw new ResourceNotFoundException("Municipio", dto.municipioId());
        }

        ZonaRestringidaEntity entity = zonaRestringidaMapper.toEntity(dto);
        entity = zonaRestringidaJpaRepository.save(entity);
        log.info("Zona restringida '{}' ({}) creada en municipio {}", entity.getNombre(), entity.getTipo(), dto.municipioId());
        return zonaRestringidaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ZonaRestringidaDto obtenerPorId(Long id) {
        ZonaRestringidaEntity entity = zonaRestringidaJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona restringida", id));
        return zonaRestringidaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ZonaRestringidaDto> listarPorMunicipio(Long municipioId, TipoZonaRest tipo) {
        List<ZonaRestringidaEntity> entities = (tipo != null)
                ? zonaRestringidaJpaRepository.findByMunicipioIdAndTipo(municipioId, tipo)
                : zonaRestringidaJpaRepository.findByMunicipioId(municipioId);
        return zonaRestringidaMapper.toDtoList(entities);
    }
}
