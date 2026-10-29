package mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.ReglaMoraDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.ReglaMoraEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReglaMoraMapper {

    ReglaMoraDto toDto(ReglaMoraEntity entity);

    List<ReglaMoraDto> toDtoList(List<ReglaMoraEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    ReglaMoraEntity toEntity(ReglaMoraCreateDto dto);
}
