package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import mx.gob.chiapas.gestamercado.api.domain.acceso.model.DispositivoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DispositivoJpaRepository extends JpaRepository<DispositivoEntity, UUID> {

    Optional<DispositivoEntity> findBySerieFolio(String serieFolio);

    boolean existsBySerieFolio(String serieFolio);

    List<DispositivoEntity> findByUsuarioId(UUID usuarioId);

    List<DispositivoEntity> findByUsuarioIdAndActivoTrue(UUID usuarioId);

    Page<DispositivoEntity> findByMercadoId(Long mercadoId, Pageable pageable);

    Page<DispositivoEntity> findByActivo(boolean activo, Pageable pageable);
}
