package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioRolJpaRepository extends JpaRepository<UsuarioRolEntity, UUID> {

    List<UsuarioRolEntity> findByUsuarioId(UUID usuarioId);

    List<UsuarioRolEntity> findByUsuarioIdAndActivoTrue(UUID usuarioId);

    boolean existsByUsuarioIdAndRolIdAndMercadoIdAndActivoTrue(UUID usuarioId, Short rolId, Long mercadoId);

    boolean existsByUsuarioIdAndRolIdAndMunicipioIdAndActivoTrue(UUID usuarioId, Short rolId, Long municipioId);
}
