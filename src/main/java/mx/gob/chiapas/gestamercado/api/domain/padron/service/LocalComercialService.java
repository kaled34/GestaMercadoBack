package mx.gob.chiapas.gestamercado.api.domain.padron.service;

import jakarta.persistence.EntityManager;
import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ConflictException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.PadronImportResultDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.PadronImportRowDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.mapper.LocalComercialMapper;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.LocalComercialEntity;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalComercialService {

    private static final Logger log = LoggerFactory.getLogger(LocalComercialService.class);

    private final LocalComercialJpaRepository localRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final LocalComercialMapper localMapper;
    private final GeoJsonMapper geoJsonMapper;
    private final EntityManager entityManager;

    public LocalComercialService(LocalComercialJpaRepository localRepository,
                                 MercadoJpaRepository mercadoRepository,
                                 LocalComercialMapper localMapper,
                                 GeoJsonMapper geoJsonMapper,
                                 EntityManager entityManager) {
        this.localRepository = localRepository;
        this.mercadoRepository = mercadoRepository;
        this.localMapper = localMapper;
        this.geoJsonMapper = geoJsonMapper;
        this.entityManager = entityManager;
    }

    @Transactional
    public LocalComercialDto crear(LocalComercialCreateDto dto) {
        if (!mercadoRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        String numLocal = dto.numeroLocal().trim();
        if (localRepository.existsByMercadoIdAndNumeroLocal(dto.mercadoId(), numLocal)) {
            throw new ConflictException("Ya existe el local número '" + numLocal + "' en el mercado " + dto.mercadoId());
        }

        validarRestriccionesArea(dto.areaOficialM2(), dto.areaOficialMotivo(), dto.metrosLineales());

        LocalComercialEntity entity = localMapper.toEntity(dto);
        entity.setNumeroLocal(numLocal);
        entity.setCreadoEn(Instant.now());
        entity.setActualizadoEn(Instant.now());

        entity = localRepository.save(entity);
        // Flush y refresh para recuperar columnas calculadas por el trigger trg_local_geometria()
        entityManager.flush();
        entityManager.refresh(entity);

        log.info("Local comercial '{}' creado con éxito en mercado {} con ID: {}", numLocal, dto.mercadoId(), entity.getId());
        return localMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public LocalComercialDto obtenerPorId(UUID id) {
        LocalComercialEntity entity = localRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local comercial", id));
        return localMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<LocalComercialDto> listar(Long mercadoId, Long zonaId, EstatusLocal estatus, Pageable pageable) {
        Page<LocalComercialEntity> pagina;
        if (zonaId != null) {
            pagina = localRepository.findByMercadoIdAndZonaId(mercadoId, zonaId, pageable);
        } else if (estatus != null) {
            pagina = localRepository.findByMercadoIdAndEstatus(mercadoId, estatus, pageable);
        } else {
            pagina = localRepository.findByMercadoId(mercadoId, pageable);
        }
        return pagina.map(localMapper::toDto);
    }

    @Transactional
    public LocalComercialDto actualizar(UUID id, LocalComercialUpdateDto dto) {
        LocalComercialEntity entity = localRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local comercial", id));

        validarRestriccionesArea(dto.areaOficialM2(), dto.areaOficialMotivo(), dto.metrosLineales());

        if (dto.zonaId() != null) entity.setZonaId(dto.zonaId());
        if (dto.nombreComercial() != null) entity.setNombreComercial(dto.nombreComercial());
        if (dto.folioTarjeton() != null) entity.setFolioTarjeton(dto.folioTarjeton());
        if (dto.tipoEspacio() != null) entity.setTipoEspacio(dto.tipoEspacio());
        if (dto.estatus() != null) entity.setEstatus(dto.estatus());
        if (dto.giroObservadoId() != null) entity.setGiroObservadoId(dto.giroObservadoId());
        if (dto.geom() != null) entity.setGeom(geoJsonMapper.toPolygonEntity(dto.geom()));
        if (dto.areaOficialM2() != null) entity.setAreaOficialM2(dto.areaOficialM2());
        if (dto.areaOficialMotivo() != null) entity.setAreaOficialMotivo(dto.areaOficialMotivo());
        if (dto.metrosLineales() != null) entity.setMetrosLineales(dto.metrosLineales());
        if (dto.notas() != null) entity.setNotas(dto.notas());
        entity.setActualizadoEn(Instant.now());

        entity = localRepository.save(entity);
        entityManager.flush();
        entityManager.refresh(entity);

        log.info("Local comercial {} actualizado", id);
        return localMapper.toDto(entity);
    }

    @Transactional
    public PadronImportResultDto importarPadron(Long mercadoId, List<PadronImportRowDto> filas) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado", mercadoId);
        }

        int exitosos = 0;
        int omitidos = 0;
        List<String> mensajes = new ArrayList<>();

        for (int i = 0; i < filas.size(); i++) {
            PadronImportRowDto fila = filas.get(i);
            try {
                String numLocal = (fila.numeroLocal() != null) ? fila.numeroLocal().trim() : null;
                if (numLocal == null || numLocal.isBlank()) {
                    omitidos++;
                    mensajes.add("Fila " + (i + 1) + ": Omitida por número de local vacío");
                    continue;
                }

                if (localRepository.existsByMercadoIdAndNumeroLocal(mercadoId, numLocal)) {
                    omitidos++;
                    mensajes.add("Fila " + (i + 1) + ": Local '" + numLocal + "' ya existe en el mercado");
                    continue;
                }

                TipoEspacio tipoEspacio = TipoEspacio.FIJO;
                if (fila.tipoEspacio() != null) {
                    try {
                        tipoEspacio = TipoEspacio.valueOf(fila.tipoEspacio().trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }

                EstatusLocal estatus = EstatusLocal.OCUPADO;
                if (fila.estatus() != null) {
                    try {
                        estatus = EstatusLocal.valueOf(fila.estatus().trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }

                LocalComercialEntity local = new LocalComercialEntity();
                local.setMercadoId(mercadoId);
                local.setNumeroLocal(numLocal);
                local.setNombreComercial(fila.nombreComercial());
                local.setFolioTarjeton(fila.folioTarjeton());
                local.setTipoEspacio(tipoEspacio);
                local.setEstatus(estatus);
                local.setAreaOficialM2(fila.areaOficialM2());
                if (fila.areaOficialM2() != null) {
                    local.setAreaOficialMotivo("Cédula de importación masiva");
                }
                local.setMetrosLineales(fila.metrosLineales());
                local.setCreadoEn(Instant.now());
                local.setActualizadoEn(Instant.now());

                localRepository.save(local);
                exitosos++;
            } catch (Exception ex) {
                omitidos++;
                mensajes.add("Fila " + (i + 1) + ": Error al importar - " + ex.getMessage());
            }
        }

        log.info("Importación de padrón para mercado {}: {} exitosos, {} omitidos", mercadoId, exitosos, omitidos);
        return new PadronImportResultDto(filas.size(), exitosos, omitidos, mensajes);
    }

    private void validarRestriccionesArea(BigDecimal areaOficialM2, String areaOficialMotivo, BigDecimal metrosLineales) {
        if (areaOficialM2 != null) {
            if (areaOficialM2.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("El área oficial debe ser mayor a 0 m²");
            }
            if (areaOficialMotivo == null || areaOficialMotivo.trim().isEmpty()) {
                throw new BusinessRuleException("Si se asigna área oficial debe justificarse el motivo formal de su asignación");
            }
        }
        if (metrosLineales != null && metrosLineales.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Los metros lineales deben ser mayores a 0");
        }
    }
}
