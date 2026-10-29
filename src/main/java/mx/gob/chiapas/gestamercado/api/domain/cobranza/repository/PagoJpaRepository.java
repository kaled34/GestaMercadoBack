package mx.gob.chiapas.gestamercado.api.domain.cobranza.repository;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagoJpaRepository extends JpaRepository<PagoEntity, UUID> {

    List<PagoEntity> findByLocalId(UUID localId);

    List<PagoEntity> findByMercadoId(Long mercadoId);

    List<PagoEntity> findByCobradorId(UUID cobradorId);

    List<PagoEntity> findByCorteCajaId(UUID corteCajaId);

    Optional<PagoEntity> findByFolio(String folio);

    List<PagoEntity> findByDepositoId(UUID depositoId);
}
