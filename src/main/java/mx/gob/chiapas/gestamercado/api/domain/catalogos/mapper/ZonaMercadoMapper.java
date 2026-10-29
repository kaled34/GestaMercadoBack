package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaMercadoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface ZonaMercadoMapper {

    @Mapping(target = "geom", source = "geom")
    ZonaMercadoDto toDto(ZonaMercadoEntity entity);

    List<ZonaMercadoDto> toDtoList(List<ZonaMercadoEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "geom", source = "geom")
    ZonaMercadoEntity toEntity(ZonaMercadoCreateDto dto);
}
