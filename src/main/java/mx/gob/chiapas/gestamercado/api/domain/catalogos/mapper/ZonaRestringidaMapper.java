package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.ZonaRestringidaDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.ZonaRestringidaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface ZonaRestringidaMapper {

    @Mapping(target = "geom", source = "geom")
    ZonaRestringidaDto toDto(ZonaRestringidaEntity entity);

    List<ZonaRestringidaDto> toDtoList(List<ZonaRestringidaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "geom", source = "geom")
    ZonaRestringidaEntity toEntity(ZonaRestringidaCreateDto dto);
}
