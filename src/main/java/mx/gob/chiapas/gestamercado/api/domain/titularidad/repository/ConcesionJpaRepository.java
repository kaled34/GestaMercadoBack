package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoConcesion;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConcesionJpaRepository extends JpaRepository<ConcesionEntity, UUID> {

    Optional<ConcesionEntity> findByLocalIdAndEstado(UUID localId, EstadoConcesion estado);

    List<ConcesionEntity> findByLocalId(UUID localId);

    List<ConcesionEntity> findByTitularPersonaId(UUID titularPersonaId);

    List<ConcesionEntity> findByMercadoId(Long mercadoId);

    List<ConcesionEntity> findByMercadoIdAndEstado(Long mercadoId, EstadoConcesion estado);

    boolean existsByMunicipioIdAndTitularPersonaIdAndEstadoInAndEsTemporalFeriaFalse(
            Long municipioId, UUID titularPersonaId, List<EstadoConcesion> estados);
}
