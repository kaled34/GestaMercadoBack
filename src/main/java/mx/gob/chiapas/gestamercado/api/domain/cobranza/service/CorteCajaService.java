package mx.gob.chiapas.gestamercado.api.domain.cobranza.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaCerrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.JornadaSinLiquidarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper.CorteCajaMapper;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.CorteCajaDiarioEntity;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.CobranzaJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.CorteCajaDiarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CorteCajaService {

    private static final Logger log = LoggerFactory.getLogger(CorteCajaService.class);

    private final CorteCajaDiarioJpaRepository corteRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final CobranzaJdbcRepository cobranzaJdbcRepository;
    private final CorteCajaMapper corteMapper;

    public CorteCajaService(CorteCajaDiarioJpaRepository corteRepository,
                            MercadoJpaRepository mercadoRepository,
                            CobranzaJdbcRepository cobranzaJdbcRepository,
                            CorteCajaMapper corteMapper) {
        this.corteRepository = corteRepository;
        this.mercadoRepository = mercadoRepository;
        this.cobranzaJdbcRepository = cobranzaJdbcRepository;
        this.corteMapper = corteMapper;
    }

    @Transactional
    public CorteCajaDto cerrarCorte(CorteCajaCerrarDto dto) {
        if (!mercadoRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        UUID corteId = cobranzaJdbcRepository.cerrarCorteCaja(dto);
        log.info("Corte ciego de caja cerrado: id={}, cobrador={}, montoDeclarado={}",
                corteId, dto.cobradorId(), dto.montoDeclarado());
        return obtenerPorId(corteId);
    }

    @Transactional(readOnly = true)
    public List<JornadaSinLiquidarDto> listarJornadasSinLiquidar(UUID cobradorId, Long mercadoId) {
        return cobranzaJdbcRepository.listarJornadasSinLiquidar(cobradorId, mercadoId);
    }

    @Transactional(readOnly = true)
    public Boolean puedeAbrirJornada(UUID cobradorId, Long mercadoId) {
        return cobranzaJdbcRepository.puedeAbrirJornada(cobradorId, mercadoId);
    }

    @Transactional(readOnly = true)
    public CorteCajaDto obtenerPorId(UUID id) {
        CorteCajaDiarioEntity entity = corteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corte de caja", id));
        return corteMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<CorteCajaDto> listarPorCobrador(UUID cobradorId) {
        return corteMapper.toDtoList(corteRepository.findByCobradorId(cobradorId));
    }

    @Transactional(readOnly = true)
    public List<CorteCajaDto> listarPorMercado(Long mercadoId) {
        return corteMapper.toDtoList(corteRepository.findByMercadoId(mercadoId));
    }
}
