package mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.TarifaDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.TarifaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TarifaMapper {

    TarifaDto toDto(TarifaEntity entity);

    List<TarifaDto> toDtoList(List<TarifaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "creadoEn", ignore = true)
    TarifaEntity toEntity(TarifaCreateDto dto);
}
