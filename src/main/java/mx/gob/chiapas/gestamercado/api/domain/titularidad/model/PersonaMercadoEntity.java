package mx.gob.chiapas.gestamercado.api.domain.titularidad.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona_mercado", schema = "gesta")
public class PersonaMercadoEntity {

    @EmbeddedId
    private PersonaMercadoId id;

    public PersonaMercadoEntity() {
    }

    public PersonaMercadoEntity(PersonaMercadoId id) {
        this.id = id;
    }

    public PersonaMercadoEntity(java.util.UUID personaId, Long mercadoId) {
        this.id = new PersonaMercadoId(personaId, mercadoId);
    }

    public PersonaMercadoId getId() {
        return id;
    }

    public void setId(PersonaMercadoId id) {
        this.id = id;
    }
}
