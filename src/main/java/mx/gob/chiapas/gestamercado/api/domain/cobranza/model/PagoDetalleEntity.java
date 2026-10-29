package mx.gob.chiapas.gestamercado.api.domain.cobranza.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pago_detalle", schema = "gesta")
public class PagoDetalleEntity {

    @EmbeddedId
    private PagoDetalleId id;

    @Column(name = "monto_aplicado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoAplicado;

    public PagoDetalleEntity() {
    }

    public PagoDetalleEntity(PagoDetalleId id, BigDecimal montoAplicado) {
        this.id = id;
        this.montoAplicado = montoAplicado;
    }

    public PagoDetalleEntity(UUID pagoId, UUID cargoId, BigDecimal montoAplicado) {
        this.id = new PagoDetalleId(pagoId, cargoId);
        this.montoAplicado = montoAplicado;
    }

    public PagoDetalleId getId() {
        return id;
    }

    public void setId(PagoDetalleId id) {
        this.id = id;
    }

    public BigDecimal getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(BigDecimal montoAplicado) {
        this.montoAplicado = montoAplicado;
    }
}
