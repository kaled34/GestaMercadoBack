package mx.gob.chiapas.gestamercado.api.domain.cobranza.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "corte_caja_diario", schema = "gesta")
public class CorteCajaDiarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "cobrador_id", nullable = false)
    private UUID cobradorId;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    @Column(name = "secuencia", nullable = false)
    private Short secuencia = 1;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "num_pagos", nullable = false)
    private Integer numPagos;

    @Column(name = "liquidado_en", nullable = false)
    private Instant liquidadoEn = Instant.now();

    @Column(name = "monto_declarado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDeclarado;

    @Column(name = "diferencia", precision = 12, scale = 2, insertable = false, updatable = false)
    private BigDecimal diferencia;

    @Column(name = "recibido_por")
    private UUID recibidoPor;

    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;

    public CorteCajaDiarioEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCobradorId() {
        return cobradorId;
    }

    public void setCobradorId(UUID cobradorId) {
        this.cobradorId = cobradorId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public Short getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Short secuencia) {
        this.secuencia = secuencia;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Integer getNumPagos() {
        return numPagos;
    }

    public void setNumPagos(Integer numPagos) {
        this.numPagos = numPagos;
    }

    public Instant getLiquidadoEn() {
        return liquidadoEn;
    }

    public void setLiquidadoEn(Instant liquidadoEn) {
        this.liquidadoEn = liquidadoEn;
    }

    public BigDecimal getMontoDeclarado() {
        return montoDeclarado;
    }

    public void setMontoDeclarado(BigDecimal montoDeclarado) {
        this.montoDeclarado = montoDeclarado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public UUID getRecibidoPor() {
        return recibidoPor;
    }

    public void setRecibidoPor(UUID recibidoPor) {
        this.recibidoPor = recibidoPor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
