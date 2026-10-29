package mx.gob.chiapas.gestamercado.api.domain.cobranza.repository;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.CorteCajaDiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CorteCajaDiarioJpaRepository extends JpaRepository<CorteCajaDiarioEntity, UUID> {

    List<CorteCajaDiarioEntity> findByCobradorId(UUID cobradorId);

    List<CorteCajaDiarioEntity> findByMercadoId(Long mercadoId);

    List<CorteCajaDiarioEntity> findByMercadoIdAndFechaCorte(Long mercadoId, LocalDate fechaCorte);
}
