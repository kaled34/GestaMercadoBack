package mx.gob.chiapas.gestamercado.api.domain.reportes.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.repository.MercadoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AdeudoLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.AlertaOperativaDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.EfectivoPendienteDepositoDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.LocalCuotaActualDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.PagoSinCorteDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.dto.SaldoFavorDto;
import mx.gob.chiapas.gestamercado.api.domain.reportes.repository.ReportesFiscalesJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReportesFiscalesService {

    private final ReportesFiscalesJdbcRepository reportesRepository;
    private final MercadoJpaRepository mercadoRepository;
    private final LocalComercialJpaRepository localRepository;

    public ReportesFiscalesService(ReportesFiscalesJdbcRepository reportesRepository,
                                   MercadoJpaRepository mercadoRepository,
                                   LocalComercialJpaRepository localRepository) {
        this.reportesRepository = reportesRepository;
        this.mercadoRepository = mercadoRepository;
        this.localRepository = localRepository;
    }

    @Transactional(readOnly = true)
    public List<AdeudoLocalDto> listarAdeudosPorMercado(Long mercadoId) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado", mercadoId);
        }
        return reportesRepository.listarAdeudosMercado(mercadoId);
    }

    @Transactional(readOnly = true)
    public AdeudoLocalDto obtenerAdeudoLocal(UUID localId) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        return reportesRepository.obtenerAdeudoLocal(localId)
                .orElse(new AdeudoLocalDto(localId, null, null, null, 0L, java.math.BigDecimal.ZERO, null, null, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, false));
    }

    @Transactional(readOnly = true)
    public SaldoFavorDto obtenerSaldoFavorLocal(UUID localId) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        return reportesRepository.obtenerSaldoFavorLocal(localId)
                .orElse(new SaldoFavorDto(localId, java.math.BigDecimal.ZERO, null));
    }

    @Transactional(readOnly = true)
    public List<EfectivoPendienteDepositoDto> listarEfectivoPendienteDeposito(Long mercadoId) {
        return reportesRepository.listarEfectivoPendienteDeposito(mercadoId);
    }

    @Transactional(readOnly = true)
    public List<PagoSinCorteDto> listarPagosSinCorte(Long mercadoId) {
        return reportesRepository.listarPagosSinCorte(mercadoId);
    }

    @Transactional(readOnly = true)
    public LocalCuotaActualDto obtenerCuotaActualLocal(UUID localId) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        return reportesRepository.obtenerCuotaActualLocal(localId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota actual del local", localId));
    }

    @Transactional(readOnly = true)
    public List<AlertaOperativaDto> listarAlertasPadron(Long mercadoId) {
        return reportesRepository.listarAlertasPadron(mercadoId);
    }

    @Transactional(readOnly = true)
    public List<AlertaOperativaDto> listarAlertasCobro(Long mercadoId) {
        return reportesRepository.listarAlertasCobro(mercadoId);
    }
}
