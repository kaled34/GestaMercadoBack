package mx.gob.chiapas.gestamercado.api.domain.padron.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.mapper.AsistenciaLocalMapper;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.AsistenciaLocalEntity;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.AsistenciaLocalJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AsistenciaLocalService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaLocalService.class);

    private final AsistenciaLocalJpaRepository asistenciaRepository;
    private final LocalComercialJpaRepository localRepository;
    private final AsistenciaLocalMapper asistenciaMapper;
    private final GeoJsonMapper geoJsonMapper;

    public AsistenciaLocalService(AsistenciaLocalJpaRepository asistenciaRepository,
                                  LocalComercialJpaRepository localRepository,
                                  AsistenciaLocalMapper asistenciaMapper,
                                  GeoJsonMapper geoJsonMapper) {
        this.asistenciaRepository = asistenciaRepository;
        this.localRepository = localRepository;
        this.asistenciaMapper = asistenciaMapper;
        this.geoJsonMapper = geoJsonMapper;
    }

    @Transactional
    public AsistenciaLocalDto registrar(AsistenciaLocalCreateDto dto) {
        if (!localRepository.existsById(dto.localId())) {
            throw new ResourceNotFoundException("Local comercial", dto.localId());
        }

        if (asistenciaRepository.existsByLocalIdAndFecha(dto.localId(), dto.fecha())) {
            throw new ConflictException("Ya existe un registro de inspección para el local " + dto.localId() + " en la fecha " + dto.fecha());
        }

        UUID registradoPor = SecurityUtils.getCurrentUserId().orElse(null);

        AsistenciaLocalEntity entity = asistenciaMapper.toEntity(dto);
        entity.setRegistradoPor(registradoPor);
        entity.setCreadoEn(Instant.now());

        if (dto.ubicacion() != null) {
            entity.setUbicacion(geoJsonMapper.toPointEntity(dto.ubicacion()));
        }

        entity = asistenciaRepository.save(entity);
        log.info("Inspección de local {} registrada: incidencia={}", dto.localId(), dto.tipoIncidencia());
        return asistenciaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public AsistenciaLocalDto obtenerPorId(UUID id) {
        AsistenciaLocalEntity entity = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspección de local", id));
        return asistenciaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<AsistenciaLocalDto> listarPorLocal(UUID localId, LocalDate inicio, LocalDate fin) {
        List<AsistenciaLocalEntity> entities;
        if (inicio != null && fin != null) {
            entities = asistenciaRepository.findByLocalIdAndFechaBetween(localId, inicio, fin);
        } else {
            entities = asistenciaRepository.findByLocalId(localId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        }
        return asistenciaMapper.toDtoList(entities);
    }
}
