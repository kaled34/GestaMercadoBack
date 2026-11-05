package mx.gob.chiapas.gestamercado.api.domain.acceso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dispositivo", schema = "gesta")
public class DispositivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Column(name = "mercado_id", updatable = false)
    private Long mercadoId;

    @Column(name = "alias", length = 80)
    private String alias;

    @Column(name = "modelo", length = 80)
    private String modelo;

    @Column(name = "serie_folio", nullable = false, length = 10, unique = true, updatable = false)
    private String serieFolio;

    @Column(name = "ultima_sincronizacion", insertable = false, updatable = false)
    private Instant ultimaSincronizacion;

    @Column(name = "activo", nullable = false, insertable = false, updatable = false)
    private boolean activo = true;

    @Column(name = "ultimo_consecutivo", nullable = false, insertable = false, updatable = false)
    private long ultimoConsecutivo = 0;

    /**
     * El rol gesta_app no tiene permisos SELECT/INSERT directos sobre secreto_hmac;
     * el trigger trg_dispositivo_protege y la función fn_dispositivo_rotar_secreto_hmac
     * gestionan el secreto criptográfico en base de datos.
     */
    @Transient
    private byte[] secretoHmac;

    public DispositivoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerieFolio() {
        return serieFolio;
    }

    public void setSerieFolio(String serieFolio) {
        this.serieFolio = serieFolio;
    }

    public Instant getUltimaSincronizacion() {
        return ultimaSincronizacion;
    }

    public void setUltimaSincronizacion(Instant ultimaSincronizacion) {
        this.ultimaSincronizacion = ultimaSincronizacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getUltimoConsecutivo() {
        return ultimoConsecutivo;
    }

    public void setUltimoConsecutivo(long ultimoConsecutivo) {
        this.ultimoConsecutivo = ultimoConsecutivo;
    }

    public byte[] getSecretoHmac() {
        return secretoHmac;
    }

    public void setSecretoHmac(byte[] secretoHmac) {
        this.secretoHmac = secretoHmac;
    }
}
