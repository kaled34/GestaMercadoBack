package mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionSuplenteDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionSuplenteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConcesionSuplenteMapper {

    ConcesionSuplenteDto toDto(ConcesionSuplenteEntity entity);

    List<ConcesionSuplenteDto> toDtoList(List<ConcesionSuplenteEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    ConcesionSuplenteEntity toEntity(ConcesionSuplenteCreateDto dto);
}
