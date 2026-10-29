package mx.gob.chiapas.gestamercado.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class GestaUserDetails implements UserDetails {

    private final UUID id;
    private final UUID personaId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean activo;
    private final boolean debeCambiarPass;
    private final boolean mfaHabilitado;
    private final Long municipioId;
    private final Long mercadoId;

    public GestaUserDetails(UUID id, UUID personaId, String username,
                            Collection<? extends GrantedAuthority> authorities,
                            boolean activo, boolean debeCambiarPass,
                            boolean mfaHabilitado, Long municipioId, Long mercadoId) {
        this.id = id;
        this.personaId = personaId;
        this.username = username;
        this.authorities = authorities;
        this.activo = activo;
        this.debeCambiarPass = debeCambiarPass;
        this.mfaHabilitado = mfaHabilitado;
        this.municipioId = municipioId;
        this.mercadoId = mercadoId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonaId() {
        return personaId;
    }

    public boolean isDebeCambiarPass() {
        return debeCambiarPass;
    }

    public boolean isMfaHabilitado() {
        return mfaHabilitado;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // En gesta_app no se almacena ni se expone el password_hash en memoria
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !debeCambiarPass;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
