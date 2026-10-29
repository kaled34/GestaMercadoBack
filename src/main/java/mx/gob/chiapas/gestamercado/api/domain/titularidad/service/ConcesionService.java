package mx.gob.chiapas.gestamercado.api.domain.titularidad.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.GiroComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoConcesion;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.LocalComercialEntity;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PermisoFeriaExpressDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper.ConcesionMapper;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper.ConcesionSuplenteMapper;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionSuplenteEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.ConcesionJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.ConcesionSuplenteJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.PersonaJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.TitularidadJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ConcesionService {

    private static final Logger log = LoggerFactory.getLogger(ConcesionService.class);

    private final ConcesionJpaRepository concesionRepository;
    private final ConcesionSuplenteJpaRepository suplenteRepository;
    private final LocalComercialJpaRepository localRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final GiroComercialJpaRepository giroRepository;
    private final PersonaJpaRepository personaRepository;
    private final TitularidadJdbcRepository titularidadJdbcRepository;
    private final ConcesionMapper concesionMapper;
    private final ConcesionSuplenteMapper suplenteMapper;

    public ConcesionService(ConcesionJpaRepository concesionRepository,
                            ConcesionSuplenteJpaRepository suplenteRepository,
                            LocalComercialJpaRepository localRepository,
                            MercadoJpaRepository mercadoRepository,
                            GiroComercialJpaRepository giroRepository,
                            PersonaJpaRepository personaRepository,
                            TitularidadJdbcRepository titularidadJdbcRepository,
                            ConcesionMapper concesionMapper,
                            ConcesionSuplenteMapper suplenteMapper) {
        this.concesionRepository = concesionRepository;
        this.suplenteRepository = suplenteRepository;
        this.localRepository = localRepository;
        this.mercadoRepository = mercadoRepository;
        this.giroRepository = giroRepository;
        this.personaRepository = personaRepository;
        this.titularidadJdbcRepository = titularidadJdbcRepository;
        this.concesionMapper = concesionMapper;
        this.suplenteMapper = suplenteMapper;
    }

    @Transactional
    public ConcesionDto crear(ConcesionCreateDto dto) {
        LocalComercialEntity local = localRepository.findById(dto.localId())
                .orElseThrow(() -> new ResourceNotFoundException("Local comercial", dto.localId()));

        MercadoEntity mercado = mercadoRepository.findById(local.getMercadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mercado", local.getMercadoId()));

        if (!personaRepository.existsById(dto.titularPersonaId())) {
            throw new ResourceNotFoundException("Persona titular", dto.titularPersonaId());
        }

        if (dto.ocupantePersonaId() != null && !personaRepository.existsById(dto.ocupantePersonaId())) {
            throw new ResourceNotFoundException("Persona ocupante", dto.ocupantePersonaId());
        }

        if (!giroRepository.existsById(dto.giroId())) {
            throw new ResourceNotFoundException("Giro comercial", dto.giroId());
        }

        boolean esTemporal = Boolean.TRUE.equals(dto.esTemporalFeria());
        boolean esFormal = Boolean.TRUE.equals(dto.esFormal());

        if (esFormal) {
            if (dto.numeroExpediente() == null || dto.numeroExpediente().isBlank()) {
                throw new BusinessRuleException("El número de expediente es obligatorio para concesiones formales");
            }
            if (dto.fechaDictamen() == null) {
                throw new BusinessRuleException("La fecha de dictamen es obligatoria para concesiones formales");
            }
        }

        if (esTemporal) {
            if (dto.fechaFin() == null) {
                throw new BusinessRuleException("La fecha de fin es obligatoria para permisos de feria temporal");
            }
            long dias = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin()) + 1;
            if (dias < 1 || dias > 30) {
                throw new BusinessRuleException("La duración del permiso temporal de feria debe ser de 1 a 30 días naturales (días calculados: " + dias + ")");
            }
        }

        if (!esTemporal) {
            boolean tieneOtraVigente = concesionRepository.existsByMunicipioIdAndTitularPersonaIdAndEstadoInAndEsTemporalFeriaFalse(
                    mercado.getMunicipioId(),
                    dto.titularPersonaId(),
                    List.of(EstadoConcesion.VIGENTE, EstadoConcesion.SUSPENDIDA)
            );
            if (tieneOtraVigente) {
                throw new ConflictException("La persona ya cuenta con una concesión activa en el municipio " + mercado.getMunicipioId());
            }
        }

        if (concesionRepository.findByLocalIdAndEstado(dto.localId(), EstadoConcesion.VIGENTE).isPresent()) {
            throw new ConflictException("El local comercial " + dto.localId() + " ya tiene una concesión en estado VIGENTE");
        }

        ConcesionEntity entity = concesionMapper.toEntity(dto);
        entity.setMercadoId(mercado.getId());
        entity.setMunicipioId(mercado.getMunicipioId());
        entity.setEstado(EstadoConcesion.VIGENTE);
        entity.setCreadoEn(Instant.now());

        entity = concesionRepository.save(entity);
        log.info("Concesión registrada: id={}, local={}, titular={}, mercado={}",
                entity.getId(), entity.getLocalId(), entity.getTitularPersonaId(), entity.getMercadoId());

        return concesionMapper.toDto(entity);
    }

    @Transactional
    public ConcesionDto actualizar(UUID id, ConcesionUpdateDto dto) {
        ConcesionEntity entity = concesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concesión", id));

        if (dto.giroId() != null && !giroRepository.existsById(dto.giroId())) {
            throw new ResourceNotFoundException("Giro comercial", dto.giroId());
        }

        if (dto.ocupantePersonaId() != null && !personaRepository.existsById(dto.ocupantePersonaId())) {
            throw new ResourceNotFoundException("Persona ocupante", dto.ocupantePersonaId());
        }

        concesionMapper.updateEntityFromDto(dto, entity);
        entity = concesionRepository.save(entity);
        log.info("Concesión actualizada: id={}, estado={}", id, entity.getEstado());
        return concesionMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ConcesionDto obtenerPorId(UUID id) {
        ConcesionEntity entity = concesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concesión", id));
        return concesionMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ConcesionDto> listarPorMercado(Long mercadoId) {
        return concesionMapper.toDtoList(concesionRepository.findByMercadoId(mercadoId));
    }

    @Transactional(readOnly = true)
    public List<ConcesionDto> listarPorLocal(UUID localId) {
        return concesionMapper.toDtoList(concesionRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public List<ConcesionDto> listarPorTitular(UUID titularPersonaId) {
        return concesionMapper.toDtoList(concesionRepository.findByTitularPersonaId(titularPersonaId));
    }

    @Transactional
    public ConcesionSuplenteDto agregarSuplente(ConcesionSuplenteCreateDto dto) {
        ConcesionEntity concesion = concesionRepository.findById(dto.concesionId())
                .orElseThrow(() -> new ResourceNotFoundException("Concesión", dto.concesionId()));

        if (!personaRepository.existsById(dto.personaId())) {
            throw new ResourceNotFoundException("Persona suplente", dto.personaId());
        }

        if (concesion.getTitularPersonaId().equals(dto.personaId())) {
            throw new BusinessRuleException("El titular de la concesión no puede asignarse a sí mismo como suplente");
        }

        if (suplenteRepository.existsByConcesionIdAndPersonaId(dto.concesionId(), dto.personaId())) {
            throw new ConflictException("La persona ya se encuentra registrada como suplente en esta concesión");
        }

        ConcesionSuplenteEntity entity = suplenteMapper.toEntity(dto);
        entity.setParentesco(dto.parentesco().trim().toUpperCase());
        entity.setCreadoEn(Instant.now());

        entity = suplenteRepository.save(entity);
        log.info("Suplente registrado: concesion={}, persona={}, parentesco={}",
                dto.concesionId(), dto.personaId(), entity.getParentesco());

        return suplenteMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ConcesionSuplenteDto> listarSuplentes(UUID concesionId) {
        return suplenteMapper.toDtoList(suplenteRepository.findByConcesionId(concesionId));
    }

    @Transactional
    public void eliminarSuplente(UUID suplenteId) {
        if (!suplenteRepository.existsById(suplenteId)) {
            throw new ResourceNotFoundException("Suplente de concesión", suplenteId);
        }
        suplenteRepository.deleteById(suplenteId);
        log.info("Suplente eliminado: id={}", suplenteId);
    }

    @Transactional
    public ConcesionDto registrarPermisoFeriaExpress(PermisoFeriaExpressDto dto) {
        UUID concesionId = titularidadJdbcRepository.registrarPermisoFeriaExpress(dto);
        log.info("Permiso de feria express expedido con éxito: concesionId={}, folio={}", concesionId, dto.folio());
        return obtenerPorId(concesionId);
    }
}
