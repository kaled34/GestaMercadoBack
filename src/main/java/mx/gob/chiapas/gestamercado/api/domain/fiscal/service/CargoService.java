package mx.gob.chiapas.gestamercado.api.domain.fiscal.service;

import mx.gob.chiapas.gestamercado.api.common.exception.ResourceNotFoundException;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoAjusteDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoCancelarDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoGenerarLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoGenerarLoteDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CuotaCalculadaDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper.CargoMapper;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.CargoEntity;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.repository.CargoJpaRepository;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.repository.FiscalJdbcRepository;
import mx.gob.chiapas.gestamercado.api.domain.padron.repository.LocalComercialJpaRepository;
import mx.gob.chiapas.gestamercado.api.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CargoService {

    private static final Logger log = LoggerFactory.getLogger(CargoService.class);

    private final CargoJpaRepository cargoRepository;
    private final LocalComercialJpaRepository localRepository;
    private final FiscalJdbcRepository fiscalJdbcRepository;
    private final CargoMapper cargoMapper;

    public CargoService(CargoJpaRepository cargoRepository,
                        LocalComercialJpaRepository localRepository,
                        FiscalJdbcRepository fiscalJdbcRepository,
                        CargoMapper cargoMapper) {
        this.cargoRepository = cargoRepository;
        this.localRepository = localRepository;
        this.fiscalJdbcRepository = fiscalJdbcRepository;
        this.cargoMapper = cargoMapper;
    }

    @Transactional(readOnly = true)
    public CuotaCalculadaDto calcularCuota(UUID localId, LocalDate fecha) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        return fiscalJdbcRepository.calcularCuota(localId, fecha)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa aplicable al local en la fecha indicada", localId));
    }

    @Transactional
    public CargoDto generarCargoLocal(CargoGenerarLocalDto dto) {
        if (!localRepository.existsById(dto.localId())) {
            throw new ResourceNotFoundException("Local comercial", dto.localId());
        }
        UUID cargoId = fiscalJdbcRepository.generarCargoLocal(dto.localId(), dto.inicio(), dto.fin());
        log.info("Cargo emitido para local {}: id={}", dto.localId(), cargoId);
        return obtenerPorId(cargoId);
    }

    @Transactional
    public String generarCargosLote(CargoGenerarLoteDto dto) {
        log.info("Iniciando generación masiva de cargos para mercado {} entre {} y {}",
                dto.mercadoId(), dto.inicio(), dto.fin());
        return fiscalJdbcRepository.generarCargosMercado(dto.mercadoId(), dto.inicio(), dto.fin());
    }

    @Transactional
    public Integer generarRecargos(Long mercadoId, LocalDate fecha) {
        log.info("Ejecutando motor de recargos moratorios para mercado {} al día {}", mercadoId, fecha);
        return fiscalJdbcRepository.generarRecargos(mercadoId, fecha);
    }

    @Transactional
    public CargoDto aplicarPenalizacionMorosidadDiaria(UUID localId, LocalDate fecha) {
        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local comercial", localId);
        }
        UUID recargoId = fiscalJdbcRepository.aplicarPenalizacionMorosidadDiaria(localId, fecha);
        log.info("Penalización diaria aplicada a local {}: recargoId={}", localId, recargoId);
        return obtenerPorId(recargoId);
    }

    @Transactional
    public CargoDto recalcularCargo(UUID cargoId) {
        if (!cargoRepository.existsById(cargoId)) {
            throw new ResourceNotFoundException("Cargo", cargoId);
        }
        fiscalJdbcRepository.recalcularCargo(cargoId);
        log.info("Cargo recalculado: id={}", cargoId);
        return obtenerPorId(cargoId);
    }

    @Transactional
    public CargoDto ajustarCargo(UUID cargoId, CargoAjusteDto dto) {
        if (!cargoRepository.existsById(cargoId)) {
            throw new ResourceNotFoundException("Cargo", cargoId);
        }
        fiscalJdbcRepository.ajustarCargo(cargoId, dto.nuevoMonto(), dto.motivo());
        log.info("Cargo ajustado: id={}, nuevoMonto={}", cargoId, dto.nuevoMonto());
        return obtenerPorId(cargoId);
    }

    @Transactional
    public CargoDto cancelarCargo(UUID cargoId, CargoCancelarDto dto) {
        if (!cargoRepository.existsById(cargoId)) {
            throw new ResourceNotFoundException("Cargo", cargoId);
        }
        fiscalJdbcRepository.cancelarCargo(cargoId, dto.motivo());
        log.info("Cargo cancelado: id={}, motivo={}", cargoId, dto.motivo());
        return obtenerPorId(cargoId);
    }

    @Transactional
    public Integer anularRecargosIndebidos(UUID cargoId) {
        if (!cargoRepository.existsById(cargoId)) {
            throw new ResourceNotFoundException("Cargo", cargoId);
        }
        UUID usuarioId = SecurityUtils.getCurrentUserId().orElse(null);
        Integer anulados = fiscalJdbcRepository.anularRecargosIndebidos(cargoId, usuarioId);
        log.info("Recargos indebidos anulados para cargo {}: total={}", cargoId, anulados);
        return anulados;
    }

    @Transactional(readOnly = true)
    public CargoDto obtenerPorId(UUID id) {
        CargoEntity entity = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", id));
        return cargoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<CargoDto> listarPorLocal(UUID localId) {
        return cargoMapper.toDtoList(cargoRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public List<CargoDto> listarPorLocalYEstado(UUID localId, EstadoCargo estado) {
        return cargoMapper.toDtoList(cargoRepository.findByLocalIdAndEstado(localId, estado));
    }
}
