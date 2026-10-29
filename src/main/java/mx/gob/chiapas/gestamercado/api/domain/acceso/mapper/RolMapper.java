package mx.gob.chiapas.gestamercado.api.domain.acceso.mapper;

import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.RolResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.RolEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolMapper {

    RolResponseDto toDto(RolEntity entity);

    List<RolResponseDto> toDtoList(List<RolEntity> entities);
}
