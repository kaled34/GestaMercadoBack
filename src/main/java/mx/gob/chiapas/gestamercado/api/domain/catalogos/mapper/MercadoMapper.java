package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.MercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.MercadoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface MercadoMapper {

    @Mapping(target = "diasOperacion", source = "diasOperacion", qualifiedByName = "arrayToList")
    @Mapping(target = "geomLimite", source = "geomLimite")
    MercadoDto toDto(MercadoEntity entity);

    List<MercadoDto> toDtoList(List<MercadoEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "diasOperacion", source = "diasOperacion", qualifiedByName = "listToArray")
    @Mapping(target = "geomLimite", source = "geomLimite")
    MercadoEntity toEntity(MercadoCreateDto dto);

    @Named("arrayToList")
    default List<Short> arrayToList(Short[] array) {
        if (array == null) {
            return List.of();
        }
        return Arrays.asList(array);
    }

    @Named("listToArray")
    default Short[] listToArray(List<Short> list) {
        if (list == null || list.isEmpty()) {
            return new Short[]{1, 2, 3, 4, 5, 6, 7};
        }
        return list.toArray(new Short[0]);
    }
}
