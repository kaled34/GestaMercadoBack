package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import mx.gob.chiapas.gestamercado.api.domain.acceso.model.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolJpaRepository extends JpaRepository<RolEntity, Short> {

    Optional<RolEntity> findByClave(String clave);

    boolean existsByClave(String clave);
}
