package mx.gob.chiapas.gestamercado.api.domain.padron.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.AsistenciaLocalDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.AsistenciaLocalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface AsistenciaLocalMapper {

    @Mapping(target = "ubicacion", source = "ubicacion")
    AsistenciaLocalDto toDto(AsistenciaLocalEntity entity);

    List<AsistenciaLocalDto> toDtoList(List<AsistenciaLocalEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "ubicacion", source = "ubicacion")
    AsistenciaLocalEntity toEntity(AsistenciaLocalCreateDto dto);
}
