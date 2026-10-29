package mx.gob.chiapas.gestamercado.api.domain.acceso.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad JPA para gesta.usuario.
 *
 * REGLA INQUEBRANTABLE DE SEGURIDAD:
 * gesta_app NO tiene permiso SELECT sobre 'password_hash' ni 'mfa_secreto_cifrado'.
 * Esta entidad omite intencionalmente dichas columnas para evitar violaciones de permisos
 * a nivel de motor SQL. Las operaciones criptográficas se delegan a gesta.fn_login_obtener,
 * gesta.fn_usuario_set_password y gesta.fn_usuario_set_mfa.
 */
@Entity
@Table(name = "usuario", schema = "gesta")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "persona_id")
    private UUID personaId;

    @Column(name = "username", nullable = false, length = 60, unique = true)
    private String username;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "debe_cambiar_pass", nullable = false)
    private boolean debeCambiarPass = true;

    @Column(name = "intentos_fallidos", nullable = false)
    private short intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private Instant bloqueadoHasta;

    @Column(name = "mfa_habilitado", nullable = false)
    private boolean mfaHabilitado = false;

    @Column(name = "ultimo_acceso")
    private Instant ultimoAcceso;

    @Column(name = "creado_por")
    private UUID creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UsuarioRolEntity> roles = new HashSet<>();

    public UsuarioEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPersonaId() {
        return personaId;
    }

    public void setPersonaId(UUID personaId) {
        this.personaId = personaId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isDebeCambiarPass() {
        return debeCambiarPass;
    }

    public void setDebeCambiarPass(boolean debeCambiarPass) {
        this.debeCambiarPass = debeCambiarPass;
    }

    public short getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(short intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public Instant getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(Instant bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public boolean isMfaHabilitado() {
        return mfaHabilitado;
    }

    public void setMfaHabilitado(boolean mfaHabilitado) {
        this.mfaHabilitado = mfaHabilitado;
    }

    public Instant getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Instant ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(UUID creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Set<UsuarioRolEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<UsuarioRolEntity> roles) {
        this.roles = roles;
    }

    public void addRol(UsuarioRolEntity usuarioRol) {
        roles.add(usuarioRol);
        usuarioRol.setUsuario(this);
    }

    public void removeRol(UsuarioRolEntity usuarioRol) {
        roles.remove(usuarioRol);
        usuarioRol.setUsuario(null);
    }
}
