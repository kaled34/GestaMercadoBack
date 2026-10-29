package mx.gob.chiapas.gestamercado.api.domain.fiscal.repository;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.CargoAjusteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CargoAjusteJpaRepository extends JpaRepository<CargoAjusteEntity, UUID> {

    List<CargoAjusteEntity> findByCargoId(UUID cargoId);

    List<CargoAjusteEntity> findByLocalId(UUID localId);

    List<CargoAjusteEntity> findByMercadoId(Long mercadoId);
}
