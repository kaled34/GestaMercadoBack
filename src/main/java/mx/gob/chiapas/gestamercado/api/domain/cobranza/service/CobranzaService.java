package mx.gob.chiapas.gestamercado.api.domain.cobranza.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoCobroCampoDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDetalleDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper.PagoDetalleMapper;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper.PagoMapper;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoEntity;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.CobranzaJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.PagoDetalleJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.repository.PagoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CobranzaService {

    private static final Logger log = LoggerFactory.getLogger(CobranzaService.class);

    private final PagoJpaRepository pagoRepository;
    private final PagoDetalleJpaRepository pagoDetalleRepository;
    private final LocalComercialJpaRepository localRepository;
    private final CobranzaJdbcRepository cobranzaJdbcRepository;
    private final PagoMapper pagoMapper;
    private final PagoDetalleMapper pagoDetalleMapper;

    public CobranzaService(PagoJpaRepository pagoRepository,
                           PagoDetalleJpaRepository pagoDetalleRepository,
                           LocalComercialJpaRepository localRepository,
                           CobranzaJdbcRepository cobranzaJdbcRepository,
                           PagoMapper pagoMapper,
                           PagoDetalleMapper pagoDetalleMapper) {
        this.pagoRepository = pagoRepository;
        this.pagoDetalleRepository = pagoDetalleRepository;
        this.localRepository = localRepository;
        this.cobranzaJdbcRepository = cobranzaJdbcRepository;
        this.pagoMapper = pagoMapper;
        this.pagoDetalleMapper = pagoDetalleMapper;
    }

    @Transactional
    public PagoDto registrarCobroCampo(PagoCobroCampoDto dto) {
        if (!localRepository.existsById(dto.localId())) {
            throw new ResourceNotFoundException("Local comercial", dto.localId());
        }

        UUID pagoId = cobranzaJdbcRepository.registrarCobroCampo(dto);
        log.info("Cobro en campo registrado y amortizado: id={}, folio={}", pagoId, dto.folio());
        return obtenerPorId(pagoId);
    }

    @Transactional
    public PagoDto registrarPagoCaja(PagoCajaDto dto) {
        if (!localRepository.existsById(dto.localId())) {
            throw new ResourceNotFoundException("Local comercial", dto.localId());
        }

        UUID pagoId = cobranzaJdbcRepository.registrarPagoCaja(dto);
        log.info("Pago en ventanilla/caja registrado: id={}, folio={}", pagoId, dto.folio());
        return obtenerPorId(pagoId);
    }

    @Transactional
    public String aplicarPagoACargo(UUID pagoId, UUID cargoId, BigDecimal monto) {
        if (!pagoRepository.existsById(pagoId)) {
            throw new ResourceNotFoundException("Pago", pagoId);
        }
        return cobranzaJdbcRepository.aplicarPago(pagoId, cargoId, monto);
    }

    @Transactional
    public BigDecimal aplicarSaldoFavor(UUID localId, UUID concesionId) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        return cobranzaJdbcRepository.aplicarSaldoFavorLocal(localId, concesionId);
    }

    @Transactional
    public PagoDto cancelarPago(UUID pagoId, String motivo) {
        if (!pagoRepository.existsById(pagoId)) {
            throw new ResourceNotFoundException("Pago", pagoId);
        }
        cobranzaJdbcRepository.cancelarPago(pagoId, motivo);
        log.info("Pago cancelado: id={}, motivo={}", pagoId, motivo);
        return obtenerPorId(pagoId);
    }

    @Transactional
    public PagoDto resolverRevision(UUID pagoId, String motivo) {
        if (!pagoRepository.existsById(pagoId)) {
            throw new ResourceNotFoundException("Pago", pagoId);
        }
        cobranzaJdbcRepository.resolverRevisionPago(pagoId, motivo);
        log.info("Revisión de pago resuelta: id={}, motivo={}", pagoId, motivo);
        return obtenerPorId(pagoId);
    }

    @Transactional(readOnly = true)
    public PagoDto obtenerPorId(UUID id) {
        PagoEntity entity = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        return pagoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PagoDto> listarPorLocal(UUID localId) {
        return pagoMapper.toDtoList(pagoRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public List<PagoDto> listarPorMercado(Long mercadoId) {
        return pagoMapper.toDtoList(pagoRepository.findByMercadoId(mercadoId));
    }

    @Transactional(readOnly = true)
    public List<PagoDto> listarPorCobrador(UUID cobradorId) {
        return pagoMapper.toDtoList(pagoRepository.findByCobradorId(cobradorId));
    }

    @Transactional(readOnly = true)
    public List<PagoDetalleDto> listarDetalles(UUID pagoId) {
        return pagoDetalleMapper.toDtoList(pagoDetalleRepository.findByIdPagoId(pagoId));
    }
}
