package mx.gob.chiapas.gestamercado.api.domain.cobranza.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaRegistrarDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper.DepositoHaciendaMapper;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.DepositoHaciendaEntity;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.CobranzaJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.DepositoHaciendaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepositoHaciendaService {

    private static final Logger log = LoggerFactory.getLogger(DepositoHaciendaService.class);

    private final DepositoHaciendaJpaRepository depositoRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final CobranzaJdbcRepository cobranzaJdbcRepository;
    private final DepositoHaciendaMapper depositoMapper;

    public DepositoHaciendaService(DepositoHaciendaJpaRepository depositoRepository,
                                   MercadoJpaRepository mercadoRepository,
                                   CobranzaJdbcRepository cobranzaJdbcRepository,
                                   DepositoHaciendaMapper depositoMapper) {
        this.depositoRepository = depositoRepository;
        this.mercadoRepository = mercadoRepository;
        this.cobranzaJdbcRepository = cobranzaJdbcRepository;
        this.depositoMapper = depositoMapper;
    }

    @Transactional
    public DepositoHaciendaDto registrar(DepositoHaciendaRegistrarDto dto) {
        if (!mercadoRepository.existsById(dto.mercadoId())) {
            throw new ResourceNotFoundException("Mercado", dto.mercadoId());
        }

        UUID depositoId = cobranzaJdbcRepository.registrarDepositoHacienda(dto);
        log.info("Depósito a hacienda registrado y conciliado: id={}, mercado={}, monto={}",
                depositoId, dto.mercadoId(), dto.montoDepositado());
        return obtenerPorId(depositoId);
    }

    @Transactional(readOnly = true)
    public DepositoHaciendaDto obtenerPorId(UUID id) {
        DepositoHaciendaEntity entity = depositoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depósito de hacienda", id));
        return depositoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<DepositoHaciendaDto> listarPorMercado(Long mercadoId) {
        return depositoMapper.toDtoList(depositoRepository.findByMercadoId(mercadoId));
    }
}
