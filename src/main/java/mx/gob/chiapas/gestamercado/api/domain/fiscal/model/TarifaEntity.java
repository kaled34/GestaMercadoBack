package mx.gob.chiapas.gestamercado.api.domain.fiscal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.FuenteTarifa;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tarifa", schema = "gesta")
public class TarifaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "municipio_id", nullable = false)
    private Long municipioId;

    @Column(name = "mercado_id")
    private Long mercadoId;

    @Column(name = "zona_id")
    private Long zonaId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_espacio")
    private TipoEspacio tipoEspacio;

    @Column(name = "giro_id")
    private Long giroId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "metodo_calculo", nullable = false)
    private MetodoCalculo metodoCalculo;

    @Column(name = "valor", nullable = false, precision = 12, scale = 4)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "unidad_valor", nullable = false)
    private UnidadValor unidadValor = UnidadValor.UMA;

    @Column(name = "valor_base_mensual", nullable = false)
    private Boolean valorBaseMensual = false;

    @Column(name = "metros_libres_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal metrosLibresBase = BigDecimal.ZERO;

    @Column(name = "cuota_base", nullable = false, precision = 12, scale = 4)
    private BigDecimal cuotaBase = BigDecimal.ZERO;

    @Column(name = "incluir_en_lote", nullable = false)
    private Boolean incluirEnLote = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "periodicidad", nullable = false)
    private Periodicidad periodicidad;

    @Column(name = "cuota_minima", precision = 12, scale = 2)
    private BigDecimal cuotaMinima;

    @Column(name = "cuota_maxima", precision = 12, scale = 2)
    private BigDecimal cuotaMaxima;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "fuente", nullable = false)
    private FuenteTarifa fuente;

    @Column(name = "fundamento_legal", columnDefinition = "text")
    private String fundamentoLegal;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "formula_cuota_diaria", nullable = false, length = 20)
    private String formulaCuotaDiaria = "ANUAL_365";

    public TarifaEntity() {
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

    public Long getZonaId() {
        return zonaId;
    }

    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }

    public TipoEspacio getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacio tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public Long getGiroId() {
        return giroId;
    }

    public void setGiroId(Long giroId) {
        this.giroId = giroId;
    }

    public MetodoCalculo getMetodoCalculo() {
        return metodoCalculo;
    }

    public void setMetodoCalculo(MetodoCalculo metodoCalculo) {
        this.metodoCalculo = metodoCalculo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadValor getUnidadValor() {
        return unidadValor;
    }

    public void setUnidadValor(UnidadValor unidadValor) {
        this.unidadValor = unidadValor;
    }

    public Boolean getValorBaseMensual() {
        return valorBaseMensual;
    }

    public void setValorBaseMensual(Boolean valorBaseMensual) {
        this.valorBaseMensual = valorBaseMensual;
    }

    public BigDecimal getMetrosLibresBase() {
        return metrosLibresBase;
    }

    public void setMetrosLibresBase(BigDecimal metrosLibresBase) {
        this.metrosLibresBase = metrosLibresBase;
    }

    public BigDecimal getCuotaBase() {
        return cuotaBase;
    }

    public void setCuotaBase(BigDecimal cuotaBase) {
        this.cuotaBase = cuotaBase;
    }

    public Boolean getIncluirEnLote() {
        return incluirEnLote;
    }

    public void setIncluirEnLote(Boolean incluirEnLote) {
        this.incluirEnLote = incluirEnLote;
    }

    public Periodicidad getPeriodicidad() {
        return periodicidad;
    }

    public void setPeriodicidad(Periodicidad periodicidad) {
        this.periodicidad = periodicidad;
    }

    public BigDecimal getCuotaMinima() {
        return cuotaMinima;
    }

    public void setCuotaMinima(BigDecimal cuotaMinima) {
        this.cuotaMinima = cuotaMinima;
    }

    public BigDecimal getCuotaMaxima() {
        return cuotaMaxima;
    }

    public void setCuotaMaxima(BigDecimal cuotaMaxima) {
        this.cuotaMaxima = cuotaMaxima;
    }

    public FuenteTarifa getFuente() {
        return fuente;
    }

    public void setFuente(FuenteTarifa fuente) {
        this.fuente = fuente;
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

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getFormulaCuotaDiaria() {
        return formulaCuotaDiaria;
    }

    public void setFormulaCuotaDiaria(String formulaCuotaDiaria) {
        this.formulaCuotaDiaria = formulaCuotaDiaria;
    }
}
