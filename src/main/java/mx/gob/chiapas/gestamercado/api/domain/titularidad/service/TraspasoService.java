package mx.gob.chiapas.gestamercado.api.domain.titularidad.service;

import jakarta.persistence.EntityManager;
import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.enums.ResultadoDictamen;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoTramite;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoAprobarDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCalculoDerechoDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper.TraspasoMapper;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.TraspasoEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.ConcesionJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.PersonaJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.TitularidadJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.TraspasoJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TraspasoService {

    private static final Logger log = LoggerFactory.getLogger(TraspasoService.class);

    private final TraspasoJpaRepository traspasoRepository;
    private final ConcesionJpaRepository concesionRepository;
    private final LocalComercialJpaRepository localRepository;
    private final PersonaJpaRepository personaRepository;
    private final TitularidadJdbcRepository titularidadJdbcRepository;
    private final TraspasoMapper traspasoMapper;
    private final EntityManager entityManager;

    public TraspasoService(TraspasoJpaRepository traspasoRepository,
                           ConcesionJpaRepository concesionRepository,
                           LocalComercialJpaRepository localRepository,
                           PersonaJpaRepository personaRepository,
                           TitularidadJdbcRepository titularidadJdbcRepository,
                           TraspasoMapper traspasoMapper,
                           EntityManager entityManager) {
        this.traspasoRepository = traspasoRepository;
        this.concesionRepository = concesionRepository;
        this.localRepository = localRepository;
        this.personaRepository = personaRepository;
        this.titularidadJdbcRepository = titularidadJdbcRepository;
        this.traspasoMapper = traspasoMapper;
        this.entityManager = entityManager;
    }

    @Transactional
    public TraspasoDto iniciarTraspaso(TraspasoCreateDto dto) {
        if (!localRepository.existsById(dto.localId())) {
            throw new ResourceNotFoundException("Local comercial", dto.localId());
        }

        ConcesionEntity origen = concesionRepository.findById(dto.concesionOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException("Concesión de origen", dto.concesionOrigenId()));

        if (!origen.getLocalId().equals(dto.localId())) {
            throw new BusinessRuleException("La concesión de origen no corresponde al local especificado");
        }

        if (Boolean.TRUE.equals(origen.getEsTemporalFeria())) {
            throw new BusinessRuleException("Los permisos temporales de feria no son transferibles ni admiten traspaso");
        }

        if (!personaRepository.existsById(dto.personaOrigenId())) {
            throw new ResourceNotFoundException("Persona de origen", dto.personaOrigenId());
        }

        if (!personaRepository.existsById(dto.personaDestinoId())) {
            throw new ResourceNotFoundException("Persona de destino", dto.personaDestinoId());
        }

        if (dto.personaOrigenId().equals(dto.personaDestinoId())) {
            throw new BusinessRuleException("La persona de origen y destino no pueden ser la misma");
        }

        UUID registradoPor = SecurityUtils.getCurrentUserId().orElse(null);

        TraspasoEntity entity = traspasoMapper.toEntity(dto);
        entity.setRegistradoPor(registradoPor);
        entity.setCreadoEn(Instant.now());

        if (dto.tipoTramite() == TipoTramite.INFORMAL_NOTIFICADO) {
            entity.setDictamen(ResultadoDictamen.NO_APLICA);
            entity.setRegularizado(false);
        } else {
            entity.setDictamen(ResultadoDictamen.PENDIENTE);
        }

        entity = traspasoRepository.save(entity);
        log.info("Traspaso iniciado: id={}, tramite={}, origen={}, destino={}",
                entity.getId(), entity.getTipoTramite(), dto.personaOrigenId(), dto.personaDestinoId());

        return traspasoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public TraspasoCalculoDerechoDto calcularDerecho(UUID concesionOrigenId,
                                                     UUID personaDestinoId,
                                                     BigDecimal cuotaBaseUma,
                                                     LocalDate fecha) {
        return titularidadJdbcRepository.calcularDerechoTraspaso(concesionOrigenId, personaDestinoId, cuotaBaseUma, fecha);
    }

    @Transactional
    public TraspasoDto dictaminarAprobacion(UUID traspasoId, TraspasoAprobarDto dto) {
        TraspasoEntity entity = traspasoRepository.findById(traspasoId)
                .orElseThrow(() -> new ResourceNotFoundException("Traspaso", traspasoId));

        if (entity.getDictamen() == ResultadoDictamen.APROBADO) {
            throw new BusinessRuleException("El trámite de traspaso ya fue aprobado previamente");
        }

        entity.setTipoTramite(TipoTramite.FORMAL);
        entity.setDictamen(ResultadoDictamen.APROBADO);
        entity.setNumeroExpediente(dto.numeroExpediente().trim());
        entity.setFechaDictamen(dto.fechaDictamen());
        entity.setFechaEfectiva(dto.fechaEfectiva() != null ? dto.fechaEfectiva() : LocalDate.now());
        entity.setRegularizado(Boolean.TRUE.equals(dto.regularizado()));

        entity = traspasoRepository.save(entity);
        entityManager.flush();
        entityManager.refresh(entity);

        log.info("Traspaso formalmente aprobado y dictaminado: id={}, nuevaConcesion={}",
                entity.getId(), entity.getConcesionDestinoId());

        return traspasoMapper.toDto(entity);
    }

    @Transactional
    public TraspasoDto dictaminarRechazo(UUID traspasoId, String notas) {
        TraspasoEntity entity = traspasoRepository.findById(traspasoId)
                .orElseThrow(() -> new ResourceNotFoundException("Traspaso", traspasoId));

        if (entity.getDictamen() == ResultadoDictamen.APROBADO) {
            throw new BusinessRuleException("Un traspaso aprobado formalmente no puede ser rechazado");
        }

        entity.setDictamen(ResultadoDictamen.RECHAZADO);
        if (notas != null && !notas.isBlank()) {
            entity.setNotas(entity.getNotas() != null ? entity.getNotas() + " | RECHAZO: " + notas : notas);
        }

        entity = traspasoRepository.save(entity);
        log.info("Traspaso rechazado: id={}", entity.getId());
        return traspasoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public TraspasoDto obtenerPorId(UUID id) {
        TraspasoEntity entity = traspasoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Traspaso", id));
        return traspasoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<TraspasoDto> listarPorLocal(UUID localId) {
        return traspasoMapper.toDtoList(traspasoRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public List<TraspasoDto> listarPorConcesion(UUID concesionId) {
        return traspasoMapper.toDtoList(traspasoRepository.findByConcesionOrigenId(concesionId));
    }
}
