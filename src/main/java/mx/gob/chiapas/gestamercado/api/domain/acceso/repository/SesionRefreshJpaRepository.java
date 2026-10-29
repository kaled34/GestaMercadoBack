package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import mx.gob.chiapas.gestamercado.api.domain.acceso.model.SesionRefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SesionRefreshJpaRepository extends JpaRepository<SesionRefreshEntity, UUID> {

    Optional<SesionRefreshEntity> findByTokenHash(String tokenHash);

    List<SesionRefreshEntity> findByUsuarioIdAndRevocadoEnIsNullAndExpiraEnAfter(UUID usuarioId, Instant now);

    List<SesionRefreshEntity> findByDispositivoIdAndRevocadoEnIsNull(UUID dispositivoId);
}
