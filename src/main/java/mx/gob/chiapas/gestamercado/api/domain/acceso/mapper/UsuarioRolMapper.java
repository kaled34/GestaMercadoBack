package mx.gob.chiapas.gestamercado.api.domain.acceso.mapper;

import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.UsuarioRolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.UsuarioRolEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioRolMapper {

    @Mapping(target = "rolId", source = "rol.id")
    @Mapping(target = "rolClave", source = "rol.clave")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    UsuarioRolResponseDto toDto(UsuarioRolEntity entity);

    List<UsuarioRolResponseDto> toDtoList(List<UsuarioRolEntity> entities);
}
