package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MunicipioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MunicipioJpaRepository extends JpaRepository<MunicipioEntity, Long> {

    Optional<MunicipioEntity> findByClaveInegi(String claveInegi);

    boolean existsByClaveInegi(String claveInegi);

    Page<MunicipioEntity> findByActivo(boolean activo, Pageable pageable);
}
