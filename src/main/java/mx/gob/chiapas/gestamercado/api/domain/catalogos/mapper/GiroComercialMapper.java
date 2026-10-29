package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.GiroComercialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.GiroComercialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GiroComercialMapper {

    GiroComercialDto toDto(GiroComercialEntity entity);

    List<GiroComercialDto> toDtoList(List<GiroComercialEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    GiroComercialEntity toEntity(GiroComercialCreateDto dto);
}
