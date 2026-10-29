package mx.gob.chiapas.gestamercado.api.domain.titularidad.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoConcesion;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "concesion", schema = "gesta")
public class ConcesionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "local_id", nullable = false)
    private UUID localId;

    @Column(name = "titular_persona_id", nullable = false)
    private UUID titularPersonaId;

    @Column(name = "giro_id", nullable = false)
    private Long giroId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoConcesion estado = EstadoConcesion.VIGENTE;

    @Column(name = "es_formal", nullable = false)
    private Boolean esFormal = false;

    @Column(name = "numero_expediente", length = 50)
    private String numeroExpediente;

    @Column(name = "fecha_dictamen")
    private LocalDate fechaDictamen;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio = LocalDate.now();

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "ocupante_persona_id")
    private UUID ocupantePersonaId;

    @Column(name = "es_temporal_feria", nullable = false)
    private Boolean esTemporalFeria = false;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    @Column(name = "municipio_id", nullable = false)
    private Long municipioId;

    public ConcesionEntity() {
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

    public UUID getTitularPersonaId() {
        return titularPersonaId;
    }

    public void setTitularPersonaId(UUID titularPersonaId) {
        this.titularPersonaId = titularPersonaId;
    }

    public Long getGiroId() {
        return giroId;
    }

    public void setGiroId(Long giroId) {
        this.giroId = giroId;
    }

    public EstadoConcesion getEstado() {
        return estado;
    }

    public void setEstado(EstadoConcesion estado) {
        this.estado = estado;
    }

    public Boolean getEsFormal() {
        return esFormal;
    }

    public void setEsFormal(Boolean esFormal) {
        this.esFormal = esFormal;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public LocalDate getFechaDictamen() {
        return fechaDictamen;
    }

    public void setFechaDictamen(LocalDate fechaDictamen) {
        this.fechaDictamen = fechaDictamen;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public UUID getOcupantePersonaId() {
        return ocupantePersonaId;
    }

    public void setOcupantePersonaId(UUID ocupantePersonaId) {
        this.ocupantePersonaId = ocupantePersonaId;
    }

    public Boolean getEsTemporalFeria() {
        return esTemporalFeria;
    }

    public void setEsTemporalFeria(Boolean esTemporalFeria) {
        this.esTemporalFeria = esTemporalFeria;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }
}
