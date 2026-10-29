package mx.gob.chiapas.gestamercado.api.domain.padron.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA para gesta.local_comercial.
 *
 * REGLA ARQUITECTÓNICA POSTGIS:
 * Las columnas area_calculada_m2, perimetro_m y centroide las calcula automáticamente
 * el trigger trg_local_geometria() en PostgreSQL al persistir o modificar 'geom'.
 * Están mapeadas como insertable = false, updatable = false.
 */
@Entity
@Table(name = "local_comercial", schema = "gesta")
public class LocalComercialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "zona_id")
    private Long zonaId;

    @Column(name = "numero_local", nullable = false, length = 20)
    private String numeroLocal;

    @Column(name = "nombre_comercial", length = 150)
    private String nombreComercial;

    @Column(name = "folio_tarjeton", length = 30)
    private String folioTarjeton;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_espacio", nullable = false)
    private TipoEspacio tipoEspacio = TipoEspacio.FIJO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estatus", nullable = false)
    private EstatusLocal estatus = EstatusLocal.OCUPADO;

    @Column(name = "giro_observado_id")
    private Long giroObservadoId;

    @Column(name = "geom", columnDefinition = "geometry(Polygon,4326)")
    private Polygon geom;

    @Column(name = "area_calculada_m2", insertable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal areaCalculadaM2;

    @Column(name = "perimetro_m", insertable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal perimetroM;

    @Column(name = "centroide", insertable = false, updatable = false, columnDefinition = "geometry(Point,4326)")
    private Point centroide;

    @Column(name = "area_oficial_m2", precision = 10, scale = 2)
    private BigDecimal areaOficialM2;

    @Column(name = "area_oficial_motivo", columnDefinition = "text")
    private String areaOficialMotivo;

    @Column(name = "metros_lineales", precision = 10, scale = 2)
    private BigDecimal metrosLineales;

    @Column(name = "notas", columnDefinition = "text")
    private String notas;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn = Instant.now();

    public LocalComercialEntity() {
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

    public Long getZonaId() {
        return zonaId;
    }

    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }

    public String getNumeroLocal() {
        return numeroLocal;
    }

    public void setNumeroLocal(String numeroLocal) {
        this.numeroLocal = numeroLocal;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getFolioTarjeton() {
        return folioTarjeton;
    }

    public void setFolioTarjeton(String folioTarjeton) {
        this.folioTarjeton = folioTarjeton;
    }

    public TipoEspacio getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacio tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public EstatusLocal getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusLocal estatus) {
        this.estatus = estatus;
    }

    public Long getGiroObservadoId() {
        return giroObservadoId;
    }

    public void setGiroObservadoId(Long giroObservadoId) {
        this.giroObservadoId = giroObservadoId;
    }

    public Polygon getGeom() {
        return geom;
    }

    public void setGeom(Polygon geom) {
        this.geom = geom;
    }

    public BigDecimal getAreaCalculadaM2() {
        return areaCalculadaM2;
    }

    public BigDecimal getPerimetroM() {
        return perimetroM;
    }

    public Point getCentroide() {
        return centroide;
    }

    public BigDecimal getAreaOficialM2() {
        return areaOficialM2;
    }

    public void setAreaOficialM2(BigDecimal areaOficialM2) {
        this.areaOficialM2 = areaOficialM2;
    }

    public String getAreaOficialMotivo() {
        return areaOficialMotivo;
    }

    public void setAreaOficialMotivo(String areaOficialMotivo) {
        this.areaOficialMotivo = areaOficialMotivo;
    }

    public BigDecimal getMetrosLineales() {
        return metrosLineales;
    }

    public void setMetrosLineales(BigDecimal metrosLineales) {
        this.metrosLineales = metrosLineales;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
