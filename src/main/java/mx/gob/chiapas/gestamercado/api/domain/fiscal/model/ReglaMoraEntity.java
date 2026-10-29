package mx.gob.chiapas.gestamercado.api.domain.fiscal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "regla_mora", schema = "gesta")
public class ReglaMoraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "municipio_id", nullable = false)
    private Long municipioId;

    @Column(name = "mercado_id")
    private Long mercadoId;

    @Column(name = "porcentaje_recargo", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeRecargo = BigDecimal.valueOf(1.25);

    @Column(name = "dias_gracia", nullable = false)
    private Short diasGracia = 15;

    @Column(name = "fundamento_legal", columnDefinition = "text")
    private String fundamentoLegal;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "tipo_calculo", nullable = false, length = 15)
    private String tipoCalculo = "SIMPLE";

    @Column(name = "tope_maximo_pct", nullable = false, precision = 6, scale = 2)
    private BigDecimal topeMaximoPct = BigDecimal.valueOf(100.00);

    public ReglaMoraEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public BigDecimal getPorcentajeRecargo() {
        return porcentajeRecargo;
    }

    public void setPorcentajeRecargo(BigDecimal porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }

    public Short getDiasGracia() {
        return diasGracia;
    }

    public void setDiasGracia(Short diasGracia) {
        this.diasGracia = diasGracia;
    }

    public String getFundamentoLegal() {
        return fundamentoLegal;
    }

    public void setFundamentoLegal(String fundamentoLegal) {
        this.fundamentoLegal = fundamentoLegal;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public void setVigenteDesde(LocalDate vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
    }

    public LocalDate getVigenteHasta() {
        return vigenteHasta;
    }

    public void setVigenteHasta(LocalDate vigenteHasta) {
        this.vigenteHasta = vigenteHasta;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(String tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public BigDecimal getTopeMaximoPct() {
        return topeMaximoPct;
    }

    public void setTopeMaximoPct(BigDecimal topeMaximoPct) {
        this.topeMaximoPct = topeMaximoPct;
    }
}
