package mx.gob.chiapas.gestamercado.api.domain.cobranza.repository;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.DepositoHaciendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DepositoHaciendaJpaRepository extends JpaRepository<DepositoHaciendaEntity, UUID> {

    List<DepositoHaciendaEntity> findByMercadoId(Long mercadoId);

    List<DepositoHaciendaEntity> findByMercadoIdAndFechaDepositoBetween(Long mercadoId, LocalDate inicio, LocalDate fin);
}
