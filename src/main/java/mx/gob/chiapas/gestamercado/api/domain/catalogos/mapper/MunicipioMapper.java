package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MunicipioDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MunicipioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MunicipioMapper {

    MunicipioDto toDto(MunicipioEntity entity);

    List<MunicipioDto> toDtoList(List<MunicipioEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    MunicipioEntity toEntity(MunicipioCreateDto dto);
}
