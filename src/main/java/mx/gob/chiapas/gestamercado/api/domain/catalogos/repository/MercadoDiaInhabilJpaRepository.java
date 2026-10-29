package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoDiaInhabilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MercadoDiaInhabilJpaRepository extends JpaRepository<MercadoDiaInhabilEntity, MercadoDiaInhabilEntity.MercadoDiaInhabilId> {

    List<MercadoDiaInhabilEntity> findByIdMercadoId(Long mercadoId);

    List<MercadoDiaInhabilEntity> findByIdMercadoIdAndIdFechaBetween(Long mercadoId, LocalDate inicio, LocalDate fin);

    boolean existsByIdMercadoIdAndIdFecha(Long mercadoId, LocalDate fecha);
}
