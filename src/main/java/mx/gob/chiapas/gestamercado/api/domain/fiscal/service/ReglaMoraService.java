package mx.gob.chiapas.gestamercado.api.domain.fiscal.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MunicipioJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper.ReglaMoraMapper;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.ReglaMoraEntity;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.repository.ReglaMoraJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReglaMoraService {

    private static final Logger log = LoggerFactory.getLogger(ReglaMoraService.class);

    private final ReglaMoraJpaRepository reglaMoraRepository;
    private final MunicipioJpaRepository municipioRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final ReglaMoraMapper reglaMoraMapper;

    public ReglaMoraService(ReglaMoraJpaRepository reglaMoraRepository,
                            MunicipioJpaRepository municipioRepository,
                            MercadoJpaRepository mercadoRepository,
                            ReglaMoraMapper reglaMoraMapper) {
        this.reglaMoraRepository = reglaMoraRepository;
        this.municipioRepository = municipioRepository;
        this.mercadoRepository = mercadoRepository;
        this.reglaMoraMapper = reglaMoraMapper;
    }

    @Transactional
    public ReglaMoraDto crear(ReglaMoraCreateDto dto) {
        if (!municipioRepository.existsById(dto.municipioId())) {
            throw new ResourceNotFoundException("Municipio", dto.municipioId());
        }

        if (dto.mercadoId() != null && !mercadoRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        if (dto.vigenteHasta() != null && dto.vigenteHasta().isBefore(dto.vigenteDesde())) {
            throw new BusinessRuleException("La fecha de fin de vigencia no puede ser anterior a la de inicio");
        }

        ReglaMoraEntity entity = reglaMoraMapper.toEntity(dto);
        entity.setActivo(true);
        if (entity.getTipoCalculo() == null) entity.setTipoCalculo("SIMPLE");

        entity = reglaMoraRepository.save(entity);
        log.info("Regla de mora creada: id={}, municipio={}, porcentaje={}, diasGracia={}",
                entity.getId(), entity.getMunicipioId(), entity.getPorcentajeRecargo(), entity.getDiasGracia());

        return reglaMoraMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ReglaMoraDto obtenerPorId(Long id) {
        ReglaMoraEntity entity = reglaMoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de mora", id));
        return reglaMoraMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ReglaMoraDto> listarPorMunicipio(Long municipioId) {
        return reglaMoraMapper.toDtoList(reglaMoraRepository.findByMunicipioIdAndActivoTrue(municipioId));
    }

    @Transactional(readOnly = true)
    public ReglaMoraDto obtenerActivaParaMercado(Long mercadoId) {
        return reglaMoraRepository.findFirstByMercadoIdAndActivoTrueOrderByVigenteDesdeDesc(mercadoId)
                .map(reglaMoraMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de mora activa para el mercado", mercadoId));
    }

    @Transactional
    public void desactivar(Long id) {
        ReglaMoraEntity entity = reglaMoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de mora", id));
        entity.setActivo(false);
        reglaMoraRepository.save(entity);
        log.info("Regla de mora desactivada: id={}", id);
    }
}
