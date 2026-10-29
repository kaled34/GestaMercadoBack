package mx.gob.chiapas.gestamercado.api.domain.fiscal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.ConceptoCargo;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cargo", schema = "gesta")
public class CargoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "local_id", nullable = false)
    private UUID localId;

    @Column(name = "concesion_id")
    private UUID concesionId;

    @Column(name = "tarifa_id")
    private Long tarifaId;

    @Column(name = "cargo_origen_id")
    private UUID cargoOrigenId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "concepto", nullable = false)
    private ConceptoCargo concepto = ConceptoCargo.DERECHO_PISO;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "area_m2_aplicada", precision = 10, scale = 2)
    private BigDecimal areaM2Aplicada;

    @Column(name = "metros_lineales_aplicados", precision = 10, scale = 2)
    private BigDecimal metrosLinealesAplicados;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "metodo_calculo")
    private MetodoCalculo metodoCalculo;

    @Column(name = "valor_tarifa_aplicado", precision = 12, scale = 4)
    private BigDecimal valorTarifaAplicado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "unidad_valor_aplicada")
    private UnidadValor unidadValorAplicada;

    @Column(name = "valor_uma_aplicado", precision = 10, scale = 2)
    private BigDecimal valorUmaAplicado;

    @Column(name = "cuota_unidad_aplicada", precision = 12, scale = 2)
    private BigDecimal cuotaUnidadAplicada;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "periodicidad")
    private Periodicidad periodicidad;

    @Column(name = "unidades", nullable = false, precision = 10, scale = 2)
    private BigDecimal unidades = BigDecimal.ONE;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoCargo estado = EstadoCargo.PENDIENTE;

    @Column(name = "generado_en", nullable = false, updatable = false)
    private Instant generadoEn = Instant.now();

    @Column(name = "motivo_cancelacion", columnDefinition = "text")
    private String motivoCancelacion;

    @Column(name = "cancelado_por")
    private UUID canceladoPor;

    @Column(name = "cancelado_en")
    private Instant canceladoEn;

    public CargoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLocalId() {
        return localId;
    }

    public void setLocalId(UUID localId) {
        this.localId = localId;
    }

    public UUID getConcesionId() {
        return concesionId;
    }

    public void setConcesionId(UUID concesionId) {
        this.concesionId = concesionId;
    }

    public Long getTarifaId() {
        return tarifaId;
    }

    public void setTarifaId(Long tarifaId) {
        this.tarifaId = tarifaId;
    }

    public UUID getCargoOrigenId() {
        return cargoOrigenId;
    }

    public void setCargoOrigenId(UUID cargoOrigenId) {
        this.cargoOrigenId = cargoOrigenId;
    }

    public ConceptoCargo getConcepto() {
        return concepto;
    }

    public void setConcepto(ConceptoCargo concepto) {
        this.concepto = concepto;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public BigDecimal getAreaM2Aplicada() {
        return areaM2Aplicada;
    }

    public void setAreaM2Aplicada(BigDecimal areaM2Aplicada) {
        this.areaM2Aplicada = areaM2Aplicada;
    }

    public BigDecimal getMetrosLinealesAplicados() {
        return metrosLinealesAplicados;
    }

    public void setMetrosLinealesAplicados(BigDecimal metrosLinealesAplicados) {
        this.metrosLinealesAplicados = metrosLinealesAplicados;
    }

    public MetodoCalculo getMetodoCalculo() {
        return metodoCalculo;
    }

    public void setMetodoCalculo(MetodoCalculo metodoCalculo) {
        this.metodoCalculo = metodoCalculo;
    }

    public BigDecimal getValorTarifaAplicado() {
        return valorTarifaAplicado;
    }

    public void setValorTarifaAplicado(BigDecimal valorTarifaAplicado) {
        this.valorTarifaAplicado = valorTarifaAplicado;
    }

    public UnidadValor getUnidadValorAplicada() {
        return unidadValorAplicada;
    }

    public void setUnidadValorAplicada(UnidadValor unidadValorAplicada) {
        this.unidadValorAplicada = unidadValorAplicada;
    }

    public BigDecimal getValorUmaAplicado() {
        return valorUmaAplicado;
    }

    public void setValorUmaAplicado(BigDecimal valorUmaAplicado) {
        this.valorUmaAplicado = valorUmaAplicado;
    }

    public BigDecimal getCuotaUnidadAplicada() {
        return cuotaUnidadAplicada;
    }

    public void setCuotaUnidadAplicada(BigDecimal cuotaUnidadAplicada) {
        this.cuotaUnidadAplicada = cuotaUnidadAplicada;
    }

    public Periodicidad getPeriodicidad() {
        return periodicidad;
    }

    public void setPeriodicidad(Periodicidad periodicidad) {
        this.periodicidad = periodicidad;
    }

    public BigDecimal getUnidades() {
        return unidades;
    }

    public void setUnidades(BigDecimal unidades) {
        this.unidades = unidades;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public EstadoCargo getEstado() {
        return estado;
    }

    public void setEstado(EstadoCargo estado) {
        this.estado = estado;
    }

    public Instant getGeneradoEn() {
        return generadoEn;
    }

    public void setGeneradoEn(Instant generadoEn) {
        this.generadoEn = generadoEn;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public UUID getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(UUID canceladoPor) {
        this.canceladoPor = canceladoPor;
    }

    public Instant getCanceladoEn() {
        return canceladoEn;
    }

    public void setCanceladoEn(Instant canceladoEn) {
        this.canceladoEn = canceladoEn;
    }
}
