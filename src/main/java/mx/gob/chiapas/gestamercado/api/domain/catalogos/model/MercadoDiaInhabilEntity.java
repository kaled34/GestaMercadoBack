package mx.gob.chiapas.gestamercado.api.domain.catalogos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "mercado_dia_inhabil", schema = "gesta")
public class MercadoDiaInhabilEntity {

    @Embeddable
    public static class MercadoDiaInhabilId implements Serializable {
        @Column(name = "mercado_id", nullable = false)
        private Long mercadoId;

        @Column(name = "fecha", nullable = false)
        private LocalDate fecha;

        public MercadoDiaInhabilId() {
        }

        public MercadoDiaInhabilId(Long mercadoId, LocalDate fecha) {
            this.mercadoId = mercadoId;
            this.fecha = fecha;
        }

        public Long getMercadoId() {
            return mercadoId;
        }

        public void setMercadoId(Long mercadoId) {
            this.mercadoId = mercadoId;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MercadoDiaInhabilId that = (MercadoDiaInhabilId) o;
            return Objects.equals(mercadoId, that.mercadoId) && Objects.equals(fecha, that.fecha);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mercadoId, fecha);
        }
    }

    @EmbeddedId
    private MercadoDiaInhabilId id;

    @Column(name = "motivo", nullable = false, columnDefinition = "text")
    private String motivo;

    @Column(name = "registrado_por")
    private UUID registradoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    public MercadoDiaInhabilEntity() {
    }

    public MercadoDiaInhabilEntity(Long mercadoId, LocalDate fecha, String motivo, UUID registradoPor) {
        this.id = new MercadoDiaInhabilId(mercadoId, fecha);
        this.motivo = motivo;
        this.registradoPor = registradoPor;
        this.creadoEn = Instant.now();
    }

    public MercadoDiaInhabilId getId() {
        return id;
    }

    public void setId(MercadoDiaInhabilId id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UUID getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UUID registradoPor) {
        this.registradoPor = registradoPor;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}
