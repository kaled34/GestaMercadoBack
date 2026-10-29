package mx.gob.chiapas.gestamercado.api.domain.padron.repository;

import mx.gob.chiapas.gestamercado.api.domain.padron.model.AsistenciaLocalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AsistenciaLocalJpaRepository extends JpaRepository<AsistenciaLocalEntity, UUID> {

    Optional<AsistenciaLocalEntity> findByLocalIdAndFecha(UUID localId, LocalDate fecha);

    boolean existsByLocalIdAndFecha(UUID localId, LocalDate fecha);

    List<AsistenciaLocalEntity> findByLocalIdAndFechaBetween(UUID localId, LocalDate inicio, LocalDate fin);

    Page<AsistenciaLocalEntity> findByLocalId(UUID localId, Pageable pageable);
}
