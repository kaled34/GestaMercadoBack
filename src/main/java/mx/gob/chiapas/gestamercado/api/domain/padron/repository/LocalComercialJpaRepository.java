package mx.gob.chiapas.gestamercado.api.domain.padron.repository;

import mx.gob.chiapas.gestamercado.api.domain.enums.EstatusLocal;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.LocalComercialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalComercialJpaRepository extends JpaRepository<LocalComercialEntity, UUID> {

    Optional<LocalComercialEntity> findByMercadoIdAndNumeroLocal(Long mercadoId, String numeroLocal);

    boolean existsByMercadoIdAndNumeroLocal(Long mercadoId, String numeroLocal);

    Page<LocalComercialEntity> findByMercadoId(Long mercadoId, Pageable pageable);

    Page<LocalComercialEntity> findByMercadoIdAndZonaId(Long mercadoId, Long zonaId, Pageable pageable);

    Page<LocalComercialEntity> findByMercadoIdAndEstatus(Long mercadoId, EstatusLocal estatus, Pageable pageable);

    List<LocalComercialEntity> findByMercadoId(Long mercadoId);
}
