package mx.gob.chiapas.gestamercado.api.domain.fiscal.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.GiroComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MunicipioJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.ZonaMercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper.TarifaMapper;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.TarifaEntity;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.repository.TarifaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class TarifaService {

    private static final Logger log = LoggerFactory.getLogger(TarifaService.class);

    private final TarifaJpaRepository tarifaRepository;
    private final MunicipioJpaRepository municipioRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final ZonaMercadoJpaRepository zonaRepository;
    private final GiroComercialJpaRepository giroRepository;
    private final TarifaMapper tarifaMapper;

    public TarifaService(TarifaJpaRepository tarifaRepository,
                         MunicipioJpaRepository municipioRepository,
                         MercadoJpaRepository mercadoRepository,
                         ZonaMercadoJpaRepository zonaRepository,
                         GiroComercialJpaRepository giroRepository,
                         TarifaMapper tarifaMapper) {
        this.tarifaRepository = tarifaRepository;
        this.municipioRepository = municipioRepository;
        this.mercadoRepository = mercadoRepository;
        this.zonaRepository = zonaRepository;
        this.giroRepository = giroRepository;
        this.tarifaMapper = tarifaMapper;
    }

    @Transactional
    public TarifaDto crear(TarifaCreateDto dto) {
        if (!municipioRepository.existsById(dto.municipioId())) {
            throw new ResourceNotFoundException("Municipio", dto.municipioId());
        }

        if (dto.mercadoId() != null && !mercadoRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        if (dto.zonaId() != null) {
            if (dto.mercadoId() == null) {
                throw new BusinessRuleException("Para asociar una zona a la tarifa se requiere especificar el mercado correspondiente");
            }
            if (!zonaRepository.existsById(dto.zonaId())) {
                throw new ResourceNotFoundException("Zona de mercado", dto.zonaId());
            }
        }

        if (dto.giroId() != null && !giroRepository.existsById(dto.giroId())) {
            throw new ResourceNotFoundException("Giro comercial", dto.giroId());
        }

        if (dto.metodoCalculo() == MetodoCalculo.CUOTA_BASE_MAS_EXCEDENTE) {
            if (dto.cuotaBase() == null || dto.cuotaBase().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("El método CUOTA_BASE_MAS_EXCEDENTE exige una cuota_base mayor a 0");
            }
            if (dto.metrosLibresBase() == null || dto.metrosLibresBase().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("El método CUOTA_BASE_MAS_EXCEDENTE exige metros_libres_base mayor a 0");
            }
        }

        if (Boolean.TRUE.equals(dto.valorBaseMensual()) && dto.periodicidad() != Periodicidad.DIARIA) {
            throw new BusinessRuleException("La propiedad valor_base_mensual solo es válida para tarifas con periodicidad DIARIA");
        }

        if (dto.cuotaMinima() != null && dto.cuotaMaxima() != null && dto.cuotaMaxima().compareTo(dto.cuotaMinima()) < 0) {
            throw new BusinessRuleException("La cuota máxima no puede ser menor a la cuota mínima");
        }

        if (dto.vigenteHasta() != null && dto.vigenteHasta().isBefore(dto.vigenteDesde())) {
            throw new BusinessRuleException("La fecha de fin de vigencia no puede ser anterior a la de inicio");
        }

        TarifaEntity entity = tarifaMapper.toEntity(dto);
        entity.setActivo(true);
        entity.setCreadoEn(Instant.now());

        entity = tarifaRepository.save(entity);
        log.info("Tarifa creada exitosamente: id={}, municipio={}, metodo={}, periodicidad={}",
                entity.getId(), entity.getMunicipioId(), entity.getMetodoCalculo(), entity.getPeriodicidad());

        return tarifaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public TarifaDto obtenerPorId(Long id) {
        TarifaEntity entity = tarifaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa", id));
        return tarifaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<TarifaDto> listarPorMunicipio(Long municipioId) {
        return tarifaMapper.toDtoList(tarifaRepository.findByMunicipioIdAndActivoTrue(municipioId));
    }

    @Transactional(readOnly = true)
    public List<TarifaDto> listarPorMercado(Long mercadoId) {
        return tarifaMapper.toDtoList(tarifaRepository.findByMercadoIdAndActivoTrue(mercadoId));
    }

    @Transactional
    public void desactivar(Long id) {
        TarifaEntity entity = tarifaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa", id));
        entity.setActivo(false);
        tarifaRepository.save(entity);
        log.info("Tarifa desactivada: id={}", id);
    }
}
