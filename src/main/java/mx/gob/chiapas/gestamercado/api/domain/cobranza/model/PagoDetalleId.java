package mx.gob.chiapas.gestamercado.api.domain.cobranza.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PagoDetalleId implements Serializable {

    @Column(name = "pago_id", nullable = false)
    private UUID pagoId;

    @Column(name = "cargo_id", nullable = false)
    private UUID cargoId;

    public PagoDetalleId() {
    }

    public PagoDetalleId(UUID pagoId, UUID cargoId) {
        this.pagoId = pagoId;
        this.cargoId = cargoId;
    }

    public UUID getPagoId() {
        return pagoId;
    }

    public void setPagoId(UUID pagoId) {
        this.pagoId = pagoId;
    }

    public UUID getCargoId() {
        return cargoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoDetalleId that = (PagoDetalleId) o;
        return Objects.equals(pagoId, that.pagoId) && Objects.equals(cargoId, that.cargoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pagoId, cargoId);
    }
}
