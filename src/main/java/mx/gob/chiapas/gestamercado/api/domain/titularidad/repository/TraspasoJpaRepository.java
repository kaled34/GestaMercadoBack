package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.ResultadoDictamen;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.TraspasoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TraspasoJpaRepository extends JpaRepository<TraspasoEntity, UUID> {

    List<TraspasoEntity> findByLocalId(UUID localId);

    List<TraspasoEntity> findByConcesionOrigenId(UUID concesionOrigenId);

    List<TraspasoEntity> findByDictamen(ResultadoDictamen dictamen);

    List<TraspasoEntity> findByPersonaOrigenIdOrPersonaDestinoId(UUID personaOrigenId, UUID personaDestinoId);
}
