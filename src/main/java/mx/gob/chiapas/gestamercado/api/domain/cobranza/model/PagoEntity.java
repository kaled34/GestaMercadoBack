package mx.gob.chiapas.gestamercado.api.domain.cobranza.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.CanalCobro;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoPago;
import mx.gob.chiapas.gestamercado.api.domain.enums.FormaPago;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pago", schema = "gesta")
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "local_id", nullable = false)
    private UUID localId;

    @Column(name = "persona_pagador_id")
    private UUID personaPagadorId;

    @Column(name = "cobrador_id")
    private UUID cobradorId;

    @Column(name = "dispositivo_id")
    private UUID dispositivoId;

    @Column(name = "corte_caja_id")
    private UUID corteCajaId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "canal", nullable = false)
    private CanalCobro canal = CanalCobro.CAMPO_ADMINISTRADOR;

    @Column(name = "folio", nullable = false, length = 40)
    private String folio;

    @Column(name = "hash_offline", nullable = false, length = 64)
    private String hashOffline;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "forma_pago", nullable = false)
    private FormaPago formaPago = FormaPago.EFECTIVO;

    @Column(name = "fecha_pago", nullable = false)
    private Instant fechaPago;

    @Column(name = "sincronizado_en")
    private Instant sincronizadoEn;

    @Column(name = "ubicacion_cobro", columnDefinition = "geometry(Point,4326)")
    private Point ubicacionCobro;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado = EstadoPago.VIGENTE;

    @Column(name = "motivo_cancelacion", columnDefinition = "text")
    private String motivoCancelacion;

    @Column(name = "deposito_id")
    private UUID depositoId;

    @Column(name = "requiere_revision", nullable = false)
    private Boolean requiereRevision = false;

    @Column(name = "motivo_revision", columnDefinition = "text")
    private String motivoRevision;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "folio_impreso", length = 40)
    private String folioImpreso;

    @Column(name = "lote_importacion_id")
    private UUID loteImportacionId;

    @Column(name = "fecha_dispositivo")
    private Instant fechaDispositivo;

    @Column(name = "payload_offline", columnDefinition = "text")
    private String payloadOffline;

    @Column(name = "consecutivo_dispositivo")
    private Long consecutivoDispositivo;

    public PagoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public UUID getLocalId() {
        return localId;
    }

    public void setLocalId(UUID localId) {
        this.localId = localId;
    }

    public UUID getPersonaPagadorId() {
        return personaPagadorId;
    }

    public void setPersonaPagadorId(UUID personaPagadorId) {
        this.personaPagadorId = personaPagadorId;
    }

    public UUID getCobradorId() {
        return cobradorId;
    }

    public void setCobradorId(UUID cobradorId) {
        this.cobradorId = cobradorId;
    }

    public UUID getDispositivoId() {
        return dispositivoId;
    }

    public void setDispositivoId(UUID dispositivoId) {
        this.dispositivoId = dispositivoId;
    }

    public UUID getCorteCajaId() {
        return corteCajaId;
    }

    public void setCorteCajaId(UUID corteCajaId) {
        this.corteCajaId = corteCajaId;
    }

    public CanalCobro getCanal() {
        return canal;
    }

    public void setCanal(CanalCobro canal) {
        this.canal = canal;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getHashOffline() {
        return hashOffline;
    }

    public void setHashOffline(String hashOffline) {
        this.hashOffline = hashOffline;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Instant getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Instant fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Instant getSincronizadoEn() {
        return sincronizadoEn;
    }

    public void setSincronizadoEn(Instant sincronizadoEn) {
        this.sincronizadoEn = sincronizadoEn;
    }

    public Point getUbicacionCobro() {
        return ubicacionCobro;
    }

    public void setUbicacionCobro(Point ubicacionCobro) {
        this.ubicacionCobro = ubicacionCobro;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public UUID getDepositoId() {
        return depositoId;
    }

    public void setDepositoId(UUID depositoId) {
        this.depositoId = depositoId;
    }

    public Boolean getRequiereRevision() {
        return requiereRevision;
    }

    public void setRequiereRevision(Boolean requiereRevision) {
        this.requiereRevision = requiereRevision;
    }

    public String getMotivoRevision() {
        return motivoRevision;
    }

    public void setMotivoRevision(String motivoRevision) {
        this.motivoRevision = motivoRevision;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getFolioImpreso() {
        return folioImpreso;
    }

    public void setFolioImpreso(String folioImpreso) {
        this.folioImpreso = folioImpreso;
    }

    public UUID getLoteImportacionId() {
        return loteImportacionId;
    }

    public void setLoteImportacionId(UUID loteImportacionId) {
        this.loteImportacionId = loteImportacionId;
    }

    public Instant getFechaDispositivo() {
        return fechaDispositivo;
    }

    public void setFechaDispositivo(Instant fechaDispositivo) {
        this.fechaDispositivo = fechaDispositivo;
    }

    public String getPayloadOffline() {
        return payloadOffline;
    }

    public void setPayloadOffline(String payloadOffline) {
        this.payloadOffline = payloadOffline;
    }

    public Long getConsecutivoDispositivo() {
        return consecutivoDispositivo;
    }

    public void setConsecutivoDispositivo(Long consecutivoDispositivo) {
        this.consecutivoDispositivo = consecutivoDispositivo;
    }
}
