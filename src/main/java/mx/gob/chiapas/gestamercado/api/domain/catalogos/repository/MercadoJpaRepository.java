package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoJpaRepository extends JpaRepository<MercadoEntity, Long> {

    Optional<MercadoEntity> findByClave(String clave);

    boolean existsByClave(String clave);

    List<MercadoEntity> findByMunicipioId(Long municipioId);

    Page<MercadoEntity> findByMunicipioId(Long municipioId, Pageable pageable);

    Page<MercadoEntity> findByActivo(boolean activo, Pageable pageable);

    List<MercadoEntity> findByActivoTrue();
}
