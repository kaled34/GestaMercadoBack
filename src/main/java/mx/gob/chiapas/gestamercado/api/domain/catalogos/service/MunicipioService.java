package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.MunicipioMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MunicipioEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MunicipioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MunicipioService {

    private static final Logger log = LoggerFactory.getLogger(MunicipioService.class);

    private final MunicipioJpaRepository municipioJpaRepository;
    private final MunicipioMapper municipioMapper;

    public MunicipioService(MunicipioJpaRepository municipioJpaRepository, MunicipioMapper municipioMapper) {
        this.municipioJpaRepository = municipioJpaRepository;
        this.municipioMapper = municipioMapper;
    }

    @Transactional
    public MunicipioDto crear(MunicipioCreateDto dto) {
        String clave = dto.claveInegi().trim();
        if (municipioJpaRepository.existsByClaveInegi(clave)) {
            throw new ConflictException("La clave INEGI '" + clave + "' ya se encuentra registrada");
        }

        MunicipioEntity entity = municipioMapper.toEntity(dto);
        entity.setClaveInegi(clave);
        entity = municipioJpaRepository.save(entity);
        log.info("Municipio creado con éxito: {} (Clave: {})", entity.getNombre(), entity.getClaveInegi());
        return municipioMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public MunicipioDto obtenerPorId(Long id) {
        MunicipioEntity entity = municipioJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipio", id));
        return municipioMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<MunicipioDto> listar(Pageable pageable, Boolean activo) {
        Page<MunicipioEntity> pagina = (activo != null)
                ? municipioJpaRepository.findByActivo(activo, pageable)
                : municipioJpaRepository.findAll(pageable);
        return pagina.map(municipioMapper::toDto);
    }

    @Transactional
    public MunicipioDto actualizar(Long id, MunicipioCreateDto dto) {
        MunicipioEntity entity = municipioJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipio", id));

        if (!entity.getClaveInegi().equals(dto.claveInegi().trim()) &&
                municipioJpaRepository.existsByClaveInegi(dto.claveInegi().trim())) {
            throw new ConflictException("La clave INEGI '" + dto.claveInegi() + "' ya se encuentra registrada");
        }

        entity.setClaveInegi(dto.claveInegi().trim());
        entity.setNombre(dto.nombre().trim());
        if (dto.estado() != null) entity.setEstado(dto.estado());
        if (dto.zonaHoraria() != null) entity.setZonaHoraria(dto.zonaHoraria());
        entity.setDiasMaxAntiguedadCobro(dto.diasMaxAntiguedadCobro());

        entity = municipioJpaRepository.save(entity);
        log.info("Municipio {} actualizado", id);
        return municipioMapper.toDto(entity);
    }
}
