package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaMercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaMercadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonaMercadoJpaRepository extends JpaRepository<PersonaMercadoEntity, PersonaMercadoId> {

    List<PersonaMercadoEntity> findByIdPersonaId(UUID personaId);

    List<PersonaMercadoEntity> findByIdMercadoId(Long mercadoId);

    boolean existsByIdPersonaIdAndIdMercadoId(UUID personaId, Long mercadoId);
}
