package mx.gob.chiapas.gestamercado.api.domain.catalogos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.ModeloCobro;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Polygon;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "mercado", schema = "gesta")
public class MercadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "municipio_id", nullable = false)
    private Long municipioId;

    @Column(name = "clave", nullable = false, length = 20, unique = true)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "modelo_cobro", nullable = false)
    private ModeloCobro modeloCobro = ModeloCobro.ADMINISTRACION_DIRECTA;

    @Column(name = "geom_limite", columnDefinition = "geometry(Polygon,4326)")
    private Polygon geomLimite;

    @Column(name = "tolerancia_traslape_m2", nullable = false, precision = 6, scale = 2)
    private BigDecimal toleranciaTraslapeM2 = new BigDecimal("0.25");

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "dias_operacion", nullable = false)
    private Short[] diasOperacion = new Short[]{1, 2, 3, 4, 5, 6, 7};

    @Column(name = "modo_omisiones_penalizacion", nullable = false, length = 15)
    private String modoOmisionesPenalizacion = "CONSECUTIVAS";

    public MercadoEntity() {
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ModeloCobro getModeloCobro() {
        return modeloCobro;
    }

    public void setModeloCobro(ModeloCobro modeloCobro) {
        this.modeloCobro = modeloCobro;
    }

    public Polygon getGeomLimite() {
        return geomLimite;
    }

    public void setGeomLimite(Polygon geomLimite) {
        this.geomLimite = geomLimite;
    }

    public BigDecimal getToleranciaTraslapeM2() {
        return toleranciaTraslapeM2;
    }

    public void setToleranciaTraslapeM2(BigDecimal toleranciaTraslapeM2) {
        this.toleranciaTraslapeM2 = toleranciaTraslapeM2;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Short[] getDiasOperacion() {
        return diasOperacion;
    }

    public void setDiasOperacion(Short[] diasOperacion) {
        this.diasOperacion = diasOperacion;
    }

    public String getModoOmisionesPenalizacion() {
        return modoOmisionesPenalizacion;
    }

    public void setModoOmisionesPenalizacion(String modoOmisionesPenalizacion) {
        this.modoOmisionesPenalizacion = modoOmisionesPenalizacion;
    }
}
