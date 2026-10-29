package mx.gob.chiapas.gestamercado.api.domain.padron.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.dto.LocalComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.padron.model.LocalComercialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface LocalComercialMapper {

    @Mapping(target = "geom", source = "geom")
    @Mapping(target = "centroide", source = "centroide")
    LocalComercialDto toDto(LocalComercialEntity entity);

    List<LocalComercialDto> toDtoList(List<LocalComercialEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "areaCalculadaM2", ignore = true)
    @Mapping(target = "perimetroM", ignore = true)
    @Mapping(target = "centroide", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    @Mapping(target = "geom", source = "geom")
    LocalComercialEntity toEntity(LocalComercialCreateDto dto);
}
