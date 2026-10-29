package mx.gob.chiapas.gestamercado.api.domain.catalogos.service;

import mx.gob.chiapas.gestamercado.api.common.exception.BusinessRuleException;
import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaMercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaOficialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper.UmaMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.UmaMercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.CatalogosJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.UmaMercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.UmaOficialJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UmaService {

    private static final Logger log = LoggerFactory.getLogger(UmaService.class);

    private final UmaMercadoJpaRepository umaMercadoJpaRepository;
    private final UmaOficialJpaRepository umaOficialJpaRepository;
    private final MercadoJpaRepository mercadoJpaRepository;
    private final CatalogosJdbcRepository catalogosJdbcRepository;
    private final UmaMapper umaMapper;

    public UmaService(UmaMercadoJpaRepository umaMercadoJpaRepository,
                      UmaOficialJpaRepository umaOficialJpaRepository,
                      MercadoJpaRepository mercadoJpaRepository,
                      CatalogosJdbcRepository catalogosJdbcRepository,
                      UmaMapper umaMapper) {
        this.umaMercadoJpaRepository = umaMercadoJpaRepository;
        this.umaOficialJpaRepository = umaOficialJpaRepository;
        this.mercadoJpaRepository = mercadoJpaRepository;
        this.catalogosJdbcRepository = catalogosJdbcRepository;
        this.umaMapper = umaMapper;
    }

    @Transactional
    public UmaMercadoDto registrarUmaMercado(UmaMercadoCreateDto dto) {
        if (!mercadoJpaRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        // Restricción CHECK ck_uma_inicio: la UMA entra en vigor anualmente el 1 de febrero
        if (dto.vigenteDesde().getMonthValue() != 2 || dto.vigenteDesde().getDayOfMonth() != 1) {
            throw new BusinessRuleException("RN-UMA-01", "La vigencia de UMA debe iniciar obligatoriamente el 1 de febrero");
        }

        UUID registradoPor = SecurityUtils.getCurrentUserId().orElse(null);

        UmaMercadoEntity entity = new UmaMercadoEntity(
                dto.mercadoId(),
                dto.vigenteDesde(),
                dto.valorDiario(),
                dto.fuente(),
                registradoPor
        );
        entity = umaMercadoJpaRepository.save(entity);
        log.info("Valor UMA registrado para mercado {}: {} (Vigente desde: {})", dto.mercadoId(), dto.valorDiario(), dto.vigenteDesde());
        return umaMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<UmaMercadoDto> listarPorMercado(Long mercadoId) {
        return umaMapper.toMercadoDtoList(umaMercadoJpaRepository.findByIdMercadoIdOrderByIdVigenteDesdeDesc(mercadoId));
    }

    @Transactional(readOnly = true)
    public List<UmaOficialDto> listarOficiales() {
        return umaMapper.toOficialDtoList(umaOficialJpaRepository.findAllByOrderByVigenteDesdeDesc());
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerValorUma(LocalDate fecha, Long mercadoId) {
        LocalDate f = (fecha != null) ? fecha : LocalDate.now();
        return catalogosJdbcRepository.obtenerValorUma(f, mercadoId);
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerUmaSugerida(LocalDate fecha) {
        LocalDate f = (fecha != null) ? fecha : LocalDate.now();
        return catalogosJdbcRepository.obtenerUmaSugerida(f);
    }
}
