package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaMercadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaMercadoJpaRepository extends JpaRepository<ZonaMercadoEntity, Long> {

    List<ZonaMercadoEntity> findByMercadoId(Long mercadoId);

    boolean existsByMercadoIdAndNombreIgnoreCase(Long mercadoId, String nombre);
}
