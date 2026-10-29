package mx.gob.chiapas.gestamercado.api.domain.catalogos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "uma_mercado", schema = "gesta")
public class UmaMercadoEntity {

    @Embeddable
    public static class UmaMercadoId implements Serializable {
        @Column(name = "mercado_id", nullable = false)
        private Long mercadoId;

        @Column(name = "vigente_desde", nullable = false)
        private LocalDate vigenteDesde;

        public UmaMercadoId() {
        }

        public UmaMercadoId(Long mercadoId, LocalDate vigenteDesde) {
            this.mercadoId = mercadoId;
            this.vigenteDesde = vigenteDesde;
        }

        public Long getMercadoId() {
            return mercadoId;
        }

        public void setMercadoId(Long mercadoId) {
            this.mercadoId = mercadoId;
        }

        public LocalDate getVigenteDesde() {
            return vigenteDesde;
        }

        public void setVigenteDesde(LocalDate vigenteDesde) {
            this.vigenteDesde = vigenteDesde;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UmaMercadoId that = (UmaMercadoId) o;
            return Objects.equals(mercadoId, that.mercadoId) && Objects.equals(vigenteDesde, that.vigenteDesde);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mercadoId, vigenteDesde);
        }
    }

    @EmbeddedId
    private UmaMercadoId id;

    @Column(name = "valor_diario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiario;

    @Column(name = "fuente", columnDefinition = "text")
    private String fuente;

    @Column(name = "registrado_por")
    private UUID registradoPor;

    @Column(name = "registrado_en", nullable = false, updatable = false)
    private Instant registradoEn = Instant.now();

    public UmaMercadoEntity() {
    }

    public UmaMercadoEntity(Long mercadoId, LocalDate vigenteDesde, BigDecimal valorDiario, String fuente, UUID registradoPor) {
        this.id = new UmaMercadoId(mercadoId, vigenteDesde);
        this.valorDiario = valorDiario;
        this.fuente = fuente;
        this.registradoPor = registradoPor;
        this.registradoEn = Instant.now();
    }

    public UmaMercadoId getId() {
        return id;
    }

    public void setId(UmaMercadoId id) {
        this.id = id;
    }

    public BigDecimal getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(BigDecimal valorDiario) {
        this.valorDiario = valorDiario;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public UUID getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UUID registradoPor) {
        this.registradoPor = registradoPor;
    }

    public Instant getRegistradoEn() {
        return registradoEn;
    }

    public void setRegistradoEn(Instant registradoEn) {
        this.registradoEn = registradoEn;
    }
}
