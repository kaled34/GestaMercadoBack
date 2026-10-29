package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.GiroComercialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiroComercialJpaRepository extends JpaRepository<GiroComercialEntity, Long> {

    Optional<GiroComercialEntity> findByClave(String clave);

    boolean existsByClave(String clave);

    Page<GiroComercialEntity> findByActivo(boolean activo, Pageable pageable);
}
