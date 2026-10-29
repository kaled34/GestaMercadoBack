package mx.gob.chiapas.gestamercado.api.domain.cobranza.repository;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoDetalleEntity;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoDetalleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PagoDetalleJpaRepository extends JpaRepository<PagoDetalleEntity, PagoDetalleId> {

    List<PagoDetalleEntity> findByIdPagoId(UUID pagoId);

    List<PagoDetalleEntity> findByIdCargoId(UUID cargoId);
}
