package mx.gob.chiapas.gestamercado.api.domain.fiscal.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.EstadoCargo;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CargoJpaRepository extends JpaRepository<CargoEntity, UUID> {

    List<CargoEntity> findByLocalId(UUID localId);

    List<CargoEntity> findByLocalIdAndEstado(UUID localId, EstadoCargo estado);

    List<CargoEntity> findByConcesionId(UUID concesionId);

    List<CargoEntity> findByLocalIdAndPeriodoInicioBetween(UUID localId, LocalDate inicio, LocalDate fin);
}
