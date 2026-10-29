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
@Table(name = "deposito_hacienda", schema = "gesta")
public class DepositoHaciendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "fecha_deposito", nullable = false)
    private LocalDate fechaDeposito;

    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin")
    private LocalDate periodoFin;

    @Column(name = "monto_depositado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDepositado;

    @Column(name = "referencia", length = 60)
    private String referencia;

    @Column(name = "depositado_por")
    private UUID depositadoPor;

    @Column(name = "tarjeta_folio", length = 40)
    private String tarjetaFolio;

    @Column(name = "tarjeta_monto", precision = 12, scale = 2)
    private BigDecimal tarjetaMonto;

    @Column(name = "diferencia", precision = 12, scale = 2, insertable = false, updatable = false)
    private BigDecimal diferencia;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "justificacion_diferencia", columnDefinition = "text")
    private String justificacionDiferencia;

    public DepositoHaciendaEntity() {
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

    public LocalDate getFechaDeposito() {
        return fechaDeposito;
    }

    public void setFechaDeposito(LocalDate fechaDeposito) {
        this.fechaDeposito = fechaDeposito;
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

    public BigDecimal getMontoDepositado() {
        return montoDepositado;
    }

    public void setMontoDepositado(BigDecimal montoDepositado) {
        this.montoDepositado = montoDepositado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public UUID getDepositadoPor() {
        return depositadoPor;
    }

    public void setDepositadoPor(UUID depositadoPor) {
        this.depositadoPor = depositadoPor;
    }

    public String getTarjetaFolio() {
        return tarjetaFolio;
    }

    public void setTarjetaFolio(String tarjetaFolio) {
        this.tarjetaFolio = tarjetaFolio;
    }

    public BigDecimal getTarjetaMonto() {
        return tarjetaMonto;
    }

    public void setTarjetaMonto(BigDecimal tarjetaMonto) {
        this.tarjetaMonto = tarjetaMonto;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getJustificacionDiferencia() {
        return justificacionDiferencia;
    }

    public void setJustificacionDiferencia(String justificacionDiferencia) {
        this.justificacionDiferencia = justificacionDiferencia;
    }
}
