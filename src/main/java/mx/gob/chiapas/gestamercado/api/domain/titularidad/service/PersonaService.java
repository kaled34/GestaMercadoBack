package mx.gob.chiapas.gestamercado.api.domain.titularidad.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoPersona;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaRefDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper.PersonaMapper;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaMercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.PersonaJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.PersonaMercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.repository.TitularidadJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {

    private static final Logger log = LoggerFactory.getLogger(PersonaService.class);

    private final PersonaJpaRepository personaRepository;
    private final PersonaMercadoJpaRepository personaMercadoRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final TitularidadJdbcRepository titularidadJdbcRepository;
    private final PersonaMapper personaMapper;

    public PersonaService(PersonaJpaRepository personaRepository,
                          PersonaMercadoJpaRepository personaMercadoRepository,
                          MercadoJpaRepository mercadoRepository,
                          TitularidadJdbcRepository titularidadJdbcRepository,
                          PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMercadoRepository = personaMercadoRepository;
        this.mercadoRepository = mercadoRepository;
        this.titularidadJdbcRepository = titularidadJdbcRepository;
        this.personaMapper = personaMapper;
    }

    @Transactional
    public PersonaDto crear(PersonaCreateDto dto) {
        if (!mercadoRepository.existsById(dto.mercadoAltaId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoAltaId());
        }

        validarEstructuraPersona(dto.tipoPersona(), dto.nombres(), dto.apellidoPaterno(), dto.razonSocial());

        if (dto.curp() != null && !dto.curp().isBlank()) {
            String curp = dto.curp().trim().toUpperCase();
            if (personaRepository.existsByCurp(curp)) {
                throw new ConflictException("Ya existe una persona registrada con la CURP " + curp);
            }
        }

        if (dto.rfc() != null && !dto.rfc().isBlank()) {
            String rfc = dto.rfc().trim().toUpperCase();
            if (personaRepository.existsByRfc(rfc)) {
                throw new ConflictException("Ya existe una persona registrada con el RFC " + rfc);
            }
        }

        PersonaEntity entity = personaMapper.toEntity(dto);
        if (entity.getCurp() != null) entity.setCurp(entity.getCurp().trim().toUpperCase());
        if (entity.getRfc() != null) entity.setRfc(entity.getRfc().trim().toUpperCase());
        entity.setCreadoEn(Instant.now());

        entity = personaRepository.save(entity);

        PersonaMercadoEntity vinculo = new PersonaMercadoEntity(entity.getId(), dto.mercadoAltaId());
        personaMercadoRepository.save(vinculo);

        log.info("Persona creada exitosamente: id={}, tipo={}, mercadoAlta={}", entity.getId(), entity.getTipoPersona(), dto.mercadoAltaId());
        return personaMapper.toDto(entity);
    }

    @Transactional
    public PersonaDto actualizar(UUID id, PersonaUpdateDto dto) {
        PersonaEntity entity = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));

        if (dto.curp() != null && !dto.curp().isBlank()) {
            String curp = dto.curp().trim().toUpperCase();
            personaRepository.findByCurp(curp)
                    .filter(p -> !p.getId().equals(id))
                    .ifPresent(p -> {
                        throw new ConflictException("Ya existe otra persona con la CURP " + curp);
                    });
            entity.setCurp(curp);
        }

        if (dto.rfc() != null && !dto.rfc().isBlank()) {
            String rfc = dto.rfc().trim().toUpperCase();
            personaRepository.findByRfc(rfc)
                    .filter(p -> !p.getId().equals(id))
                    .ifPresent(p -> {
                        throw new ConflictException("Ya existe otra persona con el RFC " + rfc);
                    });
            entity.setRfc(rfc);
        }

        personaMapper.updateEntityFromDto(dto, entity);
        entity = personaRepository.save(entity);
        log.info("Persona actualizada: id={}", id);
        return personaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public PersonaDto obtenerPorId(UUID id) {
        PersonaEntity entity = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));
        return personaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PersonaDto> buscar(String termino) {
        List<PersonaEntity> list = (termino == null || termino.isBlank())
                ? personaRepository.findAll()
                : personaRepository.buscarPorTermino(termino.trim());
        return personaMapper.toDtoList(list);
    }

    @Transactional
    public Optional<PersonaRefDto> buscarYVincular(String curp, String rfc, Long mercadoId) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado", mercadoId);
        }
        return titularidadJdbcRepository.buscarYVincularPersona(curp, rfc, mercadoId);
    }

    private void validarEstructuraPersona(TipoPersona tipoPersona, String nombres, String apellidoPaterno, String razonSocial) {
        if (tipoPersona == TipoPersona.FISICA) {
            if (nombres == null || nombres.isBlank() || apellidoPaterno == null || apellidoPaterno.isBlank()) {
                throw new BusinessRuleException("Para personas físicas, 'nombres' y 'apellidoPaterno' son obligatorios");
            }
        } else if (tipoPersona == TipoPersona.MORAL) {
            if (razonSocial == null || razonSocial.isBlank()) {
                throw new BusinessRuleException("Para personas morales, 'razonSocial' es obligatoria");
            }
        }
    }
}
