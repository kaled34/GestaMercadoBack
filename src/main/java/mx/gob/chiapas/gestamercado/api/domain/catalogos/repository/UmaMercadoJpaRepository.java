package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.UmaMercadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UmaMercadoJpaRepository extends JpaRepository<UmaMercadoEntity, UmaMercadoEntity.UmaMercadoId> {

    List<UmaMercadoEntity> findByIdMercadoIdOrderByIdVigenteDesdeDesc(Long mercadoId);
}
