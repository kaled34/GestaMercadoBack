package mx.gob.chiapas.gestamercado.api.domain.titularidad.repository;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, UUID> {

    Optional<PersonaEntity> findByCurp(String curp);

    Optional<PersonaEntity> findByRfc(String rfc);

    List<PersonaEntity> findByMercadoAltaId(Long mercadoAltaId);

    boolean existsByCurp(String curp);

    boolean existsByRfc(String rfc);

    @Query("SELECT p FROM PersonaEntity p WHERE " +
           "(:termino IS NULL OR " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "UPPER(p.curp) = UPPER(:termino) OR " +
           "UPPER(p.rfc) = UPPER(:termino))")
    List<PersonaEntity> buscarPorTermino(@Param("termino") String termino);
}
