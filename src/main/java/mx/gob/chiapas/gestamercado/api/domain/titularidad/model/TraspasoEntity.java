package mx.gob.chiapas.gestamercado.api.domain.titularidad.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.ResultadoDictamen;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoTramite;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "traspaso", schema = "gesta")
public class TraspasoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "local_id", nullable = false)
    private UUID localId;

    @Column(name = "concesion_origen_id", nullable = false)
    private UUID concesionOrigenId;

    @Column(name = "concesion_destino_id")
    private UUID concesionDestinoId;

    @Column(name = "persona_origen_id", nullable = false)
    private UUID personaOrigenId;

    @Column(name = "persona_destino_id", nullable = false)
    private UUID personaDestinoId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_tramite", nullable = false)
    private TipoTramite tipoTramite = TipoTramite.FORMAL;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;

    @Column(name = "fecha_notificacion")
    private LocalDate fechaNotificacion;

    @Column(name = "fecha_efectiva")
    private LocalDate fechaEfectiva;

    @Column(name = "numero_expediente", length = 50)
    private String numeroExpediente;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "dictamen", nullable = false)
    private ResultadoDictamen dictamen = ResultadoDictamen.PENDIENTE;

    @Column(name = "fecha_dictamen")
    private LocalDate fechaDictamen;

    @Column(name = "notificado_a_fiscal_en")
    private LocalDate notificadoAFiscalEn;

    @Column(name = "regularizado", nullable = false)
    private Boolean regularizado = false;

    @Column(name = "registrado_por")
    private UUID registradoPor;

    @Column(name = "notas", columnDefinition = "text")
    private String notas;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "cuota_base_uma", precision = 10, scale = 2)
    private BigDecimal cuotaBaseUma = BigDecimal.valueOf(10.00);

    @Column(name = "cargo_traspaso_id")
    private UUID cargoTraspasoId;

    public TraspasoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLocalId() {
        return localId;
    }

    public void setLocalId(UUID localId) {
        this.localId = localId;
    }

    public UUID getConcesionOrigenId() {
        return concesionOrigenId;
    }

    public void setConcesionOrigenId(UUID concesionOrigenId) {
        this.concesionOrigenId = concesionOrigenId;
    }

    public UUID getConcesionDestinoId() {
        return concesionDestinoId;
    }

    public void setConcesionDestinoId(UUID concesionDestinoId) {
        this.concesionDestinoId = concesionDestinoId;
    }

    public UUID getPersonaOrigenId() {
        return personaOrigenId;
    }

    public void setPersonaOrigenId(UUID personaOrigenId) {
        this.personaOrigenId = personaOrigenId;
    }

    public UUID getPersonaDestinoId() {
        return personaDestinoId;
    }

    public void setPersonaDestinoId(UUID personaDestinoId) {
        this.personaDestinoId = personaDestinoId;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(LocalDate fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public LocalDate getFechaEfectiva() {
        return fechaEfectiva;
    }

    public void setFechaEfectiva(LocalDate fechaEfectiva) {
        this.fechaEfectiva = fechaEfectiva;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public ResultadoDictamen getDictamen() {
        return dictamen;
    }

    public void setDictamen(ResultadoDictamen dictamen) {
        this.dictamen = dictamen;
    }

    public LocalDate getFechaDictamen() {
        return fechaDictamen;
    }

    public void setFechaDictamen(LocalDate fechaDictamen) {
        this.fechaDictamen = fechaDictamen;
    }

    public LocalDate getNotificadoAFiscalEn() {
        return notificadoAFiscalEn;
    }

    public void setNotificadoAFiscalEn(LocalDate notificadoAFiscalEn) {
        this.notificadoAFiscalEn = notificadoAFiscalEn;
    }

    public Boolean getRegularizado() {
        return regularizado;
    }

    public void setRegularizado(Boolean regularizado) {
        this.regularizado = regularizado;
    }

    public UUID getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UUID registradoPor) {
        this.registradoPor = registradoPor;
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

    public BigDecimal getCuotaBaseUma() {
        return cuotaBaseUma;
    }

    public void setCuotaBaseUma(BigDecimal cuotaBaseUma) {
        this.cuotaBaseUma = cuotaBaseUma;
    }

    public UUID getCargoTraspasoId() {
        return cargoTraspasoId;
    }

    public void setCargoTraspasoId(UUID cargoTraspasoId) {
        this.cargoTraspasoId = cargoTraspasoId;
    }
}
