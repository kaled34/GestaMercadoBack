package mx.gob.chiapas.gestamercado.api.domain.catalogos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "uma_oficial", schema = "gesta")
public class UmaOficialEntity {

    @Id
    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "valor_diario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiario;

    @Column(name = "fuente", columnDefinition = "text")
    private String fuente;

    public UmaOficialEntity() {
    }

    public UmaOficialEntity(LocalDate vigenteDesde, BigDecimal valorDiario, String fuente) {
        this.vigenteDesde = vigenteDesde;
        this.valorDiario = valorDiario;
        this.fuente = fuente;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public void setVigenteDesde(LocalDate vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
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
}
