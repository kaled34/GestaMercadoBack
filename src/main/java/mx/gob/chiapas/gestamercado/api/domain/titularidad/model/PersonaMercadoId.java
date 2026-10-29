package mx.gob.chiapas.gestamercado.api.domain.titularidad.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PersonaMercadoId implements Serializable {

    @Column(name = "persona_id", nullable = false)
    private UUID personaId;

    @Column(name = "mercado_id", nullable = false)
    private Long mercadoId;

    public PersonaMercadoId() {
    }

    public PersonaMercadoId(UUID personaId, Long mercadoId) {
        this.personaId = personaId;
        this.mercadoId = mercadoId;
    }

    public UUID getPersonaId() {
        return personaId;
    }

    public void setPersonaId(UUID personaId) {
        this.personaId = personaId;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonaMercadoId that = (PersonaMercadoId) o;
        return Objects.equals(personaId, that.personaId) && Objects.equals(mercadoId, that.mercadoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personaId, mercadoId);
    }
}
