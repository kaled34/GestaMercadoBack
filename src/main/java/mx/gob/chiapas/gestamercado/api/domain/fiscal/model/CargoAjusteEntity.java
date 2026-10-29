package mx.gob.chiapas.gestamercado.api.domain.fiscal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cargo_ajuste", schema = "gesta")
public class CargoAjusteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "cargo_id", nullable = false)
    private UUID cargoId;

    @Column(name = "local_id", nullable = false)
    private UUID localId;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "monto_anterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoAnterior;

    @Column(name = "monto_nuevo", precision = 12, scale = 2)
    private BigDecimal montoNuevo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_anterior", nullable = false)
    private EstadoCargo estadoAnterior;

    @Column(name = "motivo", nullable = false, columnDefinition = "text")
    private String motivo;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    public CargoAjusteEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCargoId() {
        return cargoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }

    public UUID getLocalId() {
        return localId;
    }

    public void setLocalId(UUID localId) {
        this.localId = localId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMontoAnterior() {
        return montoAnterior;
    }

    public void setMontoAnterior(BigDecimal montoAnterior) {
        this.montoAnterior = montoAnterior;
    }

    public BigDecimal getMontoNuevo() {
        return montoNuevo;
    }

    public void setMontoNuevo(BigDecimal montoNuevo) {
        this.montoNuevo = montoNuevo;
    }

    public EstadoCargo getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoCargo estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}
