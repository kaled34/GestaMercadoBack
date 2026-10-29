package mx.gob.chiapas.gestamercado.api.domain.fiscal.repository;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.TarifaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarifaJpaRepository extends JpaRepository<TarifaEntity, Long> {

    List<TarifaEntity> findByMunicipioIdAndActivoTrue(Long municipioId);

    List<TarifaEntity> findByMercadoIdAndActivoTrue(Long mercadoId);

    List<TarifaEntity> findByZonaIdAndActivoTrue(Long zonaId);

    List<TarifaEntity> findByMunicipioId(Long municipioId);
}
