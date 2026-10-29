package mx.gob.chiapas.gestamercado.api.domain.acceso.repository;

import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {

    Optional<UsuarioEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.rol WHERE LOWER(u.username) = LOWER(:username)")
    Optional<UsuarioEntity> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.rol WHERE u.id = :id")
    Optional<UsuarioEntity> findByIdWithRoles(@Param("id") UUID id);

    Page<UsuarioEntity> findByActivo(boolean activo, Pageable pageable);
}
