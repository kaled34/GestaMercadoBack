package mx.gob.chiapas.gestamercado.api.domain.fiscal.repository;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.ReglaMoraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReglaMoraJpaRepository extends JpaRepository<ReglaMoraEntity, Long> {

    List<ReglaMoraEntity> findByMunicipioIdAndActivoTrue(Long municipioId);

    Optional<ReglaMoraEntity> findFirstByMercadoIdAndActivoTrueOrderByVigenteDesdeDesc(Long mercadoId);

    Optional<ReglaMoraEntity> findFirstByMunicipioIdAndActivoTrueOrderByVigenteDesdeDesc(Long municipioId);
}
