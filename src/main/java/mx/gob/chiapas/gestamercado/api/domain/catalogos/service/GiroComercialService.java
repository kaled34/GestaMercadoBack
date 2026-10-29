package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.GiroComercialMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.GiroComercialEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.GiroComercialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GiroComercialService {

    private static final Logger log = LoggerFactory.getLogger(GiroComercialService.class);

    private final GiroComercialJpaRepository giroComercialJpaRepository;
    private final GiroComercialMapper giroComercialMapper;

    public GiroComercialService(GiroComercialJpaRepository giroComercialJpaRepository,
                                GiroComercialMapper giroComercialMapper) {
        this.giroComercialJpaRepository = giroComercialJpaRepository;
        this.giroComercialMapper = giroComercialMapper;
    }

    @Transactional
    public GiroComercialDto crear(GiroComercialCreateDto dto) {
        String clave = dto.clave().trim().toUpperCase();
        if (giroComercialJpaRepository.existsByClave(clave)) {
            throw new ConflictException("La clave de giro comercial '" + clave + "' ya se encuentra registrada");
        }

        GiroComercialEntity entity = giroComercialMapper.toEntity(dto);
        entity.setClave(clave);
        entity = giroComercialJpaRepository.save(entity);
        log.info("Giro comercial creado: {} ({})", entity.getNombre(), entity.getClave());
        return giroComercialMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public GiroComercialDto obtenerPorId(Long id) {
        GiroComercialEntity entity = giroComercialJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giro comercial", id));
        return giroComercialMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<GiroComercialDto> listar(Pageable pageable, Boolean activo) {
        Page<GiroComercialEntity> pagina = (activo != null)
                ? giroComercialJpaRepository.findByActivo(activo, pageable)
                : giroComercialJpaRepository.findAll(pageable);
        return pagina.map(giroComercialMapper::toDto);
    }
}
