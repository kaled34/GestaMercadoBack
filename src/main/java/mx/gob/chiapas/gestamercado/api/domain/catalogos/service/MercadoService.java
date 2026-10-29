package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDiaInhabilCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDiaInhabilDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.MercadoMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoDiaInhabilEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoDiaInhabilJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MunicipioJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MercadoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoService.class);

    private final MercadoJpaRepository mercadoJpaRepository;
    private final MunicipioJpaRepository municipioJpaRepository;
    private final MercadoDiaInhabilJpaRepository diaInhabilJpaRepository;
    private final MercadoMapper mercadoMapper;
    private final GeoJsonMapper geoJsonMapper;

    public MercadoService(MercadoJpaRepository mercadoJpaRepository,
                          MunicipioJpaRepository municipioJpaRepository,
                          MercadoDiaInhabilJpaRepository diaInhabilJpaRepository,
                          MercadoMapper mercadoMapper,
                          GeoJsonMapper geoJsonMapper) {
        this.mercadoJpaRepository = mercadoJpaRepository;
        this.municipioJpaRepository = municipioJpaRepository;
        this.diaInhabilJpaRepository = diaInhabilJpaRepository;
        this.mercadoMapper = mercadoMapper;
        this.geoJsonMapper = geoJsonMapper;
    }

    @Transactional
    public MercadoDto crear(MercadoCreateDto dto) {
        String clave = dto.clave().trim().toUpperCase();
        if (mercadoJpaRepository.existsByClave(clave)) {
            throw new ConflictException("La clave de mercado '" + clave + "' ya se encuentra registrada");
        }

        if (!municipioJpaRepository.existsById(dto.municipioId())) {
            throw new ResourceNotFoundException("Municipio", dto.municipioId());
        }

        MercadoEntity entity = mercadoMapper.toEntity(dto);
        entity.setClave(clave);
        entity = mercadoJpaRepository.save(entity);
        log.info("Mercado '{}' registrado con ID {}", entity.getNombre(), entity.getId());
        return mercadoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public MercadoDto obtenerPorId(Long id) {
        MercadoEntity entity = mercadoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado", id));
        return mercadoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<MercadoDto> listar(Long municipioId, Boolean activo, Pageable pageable) {
        Page<MercadoEntity> pagina;
        if (municipioId != null) {
            pagina = mercadoJpaRepository.findByMunicipioId(municipioId, pageable);
        } else if (activo != null) {
            pagina = mercadoJpaRepository.findByActivo(activo, pageable);
        } else {
            pagina = mercadoJpaRepository.findAll(pageable);
        }
        return pagina.map(mercadoMapper::toDto);
    }

    @Transactional
    public MercadoDto actualizar(Long id, MercadoUpdateDto dto) {
        MercadoEntity entity = mercadoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado", id));

        if (dto.nombre() != null) entity.setNombre(dto.nombre().trim());
        if (dto.modeloCobro() != null) entity.setModeloCobro(dto.modeloCobro());
        if (dto.geomLimite() != null) entity.setGeomLimite(geoJsonMapper.toPolygonEntity(dto.geomLimite()));
        if (dto.toleranciaTraslapeM2() != null) entity.setToleranciaTraslapeM2(dto.toleranciaTraslapeM2());
        if (dto.activo() != null) entity.setActivo(dto.activo());
        if (dto.diasOperacion() != null) entity.setDiasOperacion(dto.diasOperacion().toArray(new Short[0]));
        if (dto.modoOmisionesPenalizacion() != null) entity.setModoOmisionesPenalizacion(dto.modoOmisionesPenalizacion());

        entity = mercadoJpaRepository.save(entity);
        log.info("Mercado {} actualizado", id);
        return mercadoMapper.toDto(entity);
    }

    @Transactional
    public MercadoDiaInhabilDto agregarDiaInhabil(MercadoDiaInhabilCreateDto dto) {
        if (!mercadoJpaRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        if (diaInhabilJpaRepository.existsByIdMercadoIdAndIdFecha(dto.mercadoId(), dto.fecha())) {
            throw new ConflictException("La fecha " + dto.fecha() + " ya está registrada como día inhábil en el mercado " + dto.mercadoId());
        }

        UUID registradoPor = SecurityUtils.getCurrentUserId().orElse(null);
        MercadoDiaInhabilEntity entity = new MercadoDiaInhabilEntity(
                dto.mercadoId(),
                dto.fecha(),
                dto.motivo().trim(),
                registradoPor
        );
        entity = diaInhabilJpaRepository.save(entity);
        log.info("Día inhábil registrado para mercado {}: {} - {}", dto.mercadoId(), dto.fecha(), dto.motivo());
        return new MercadoDiaInhabilDto(
                entity.getId().getMercadoId(),
                entity.getId().getFecha(),
                entity.getMotivo(),
                entity.getRegistradoPor(),
                entity.getCreadoEn()
        );
    }

    @Transactional(readOnly = true)
    public List<MercadoDiaInhabilDto> listarDiasInhabiles(Long mercadoId, LocalDate inicio, LocalDate fin) {
        List<MercadoDiaInhabilEntity> entities;
        if (inicio != null && fin != null) {
            entities = diaInhabilJpaRepository.findByIdMercadoIdAndIdFechaBetween(mercadoId, inicio, fin);
        } else {
            entities = diaInhabilJpaRepository.findByIdMercadoId(mercadoId);
        }
        return entities.stream()
                .map(e -> new MercadoDiaInhabilDto(
                        e.getId().getMercadoId(),
                        e.getId().getFecha(),
                        e.getMotivo(),
                        e.getRegistradoPor(),
                        e.getCreadoEn()))
                .toList();
    }
}
