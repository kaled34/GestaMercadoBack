package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.UmaOficialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UmaOficialJpaRepository extends JpaRepository<UmaOficialEntity, LocalDate> {

    List<UmaOficialEntity> findAllByOrderByVigenteDesdeDesc();
}
