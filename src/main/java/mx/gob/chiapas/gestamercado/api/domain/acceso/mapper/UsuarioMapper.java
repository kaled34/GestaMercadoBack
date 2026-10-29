package mx.gob.chiapas.gestamercado.api.domain.acceso.mapper;

import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsuarioRolMapper.class})
public interface UsuarioMapper {

    UsuarioResponseDto toDto(UsuarioEntity entity);

    List<UsuarioResponseDto> toDtoList(List<UsuarioEntity> entities);
}
