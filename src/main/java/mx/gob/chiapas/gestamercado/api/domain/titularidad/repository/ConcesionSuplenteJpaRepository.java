package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionSuplenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConcesionSuplenteJpaRepository extends JpaRepository<ConcesionSuplenteEntity, UUID> {

    List<ConcesionSuplenteEntity> findByConcesionId(UUID concesionId);

    Optional<ConcesionSuplenteEntity> findByConcesionIdAndPersonaId(UUID concesionId, UUID personaId);

    long countByConcesionId(UUID concesionId);

    boolean existsByConcesionIdAndPersonaId(UUID concesionId, UUID personaId);
}
