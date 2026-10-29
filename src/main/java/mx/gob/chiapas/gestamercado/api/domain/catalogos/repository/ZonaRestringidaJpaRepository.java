package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaRestringidaEntity;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoZonaRest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaRestringidaJpaRepository extends JpaRepository<ZonaRestringidaEntity, Long> {

    List<ZonaRestringidaEntity> findByMunicipioId(Long municipioId);

    List<ZonaRestringidaEntity> findByMunicipioIdAndTipo(Long municipioId, TipoZonaRest tipo);
}
