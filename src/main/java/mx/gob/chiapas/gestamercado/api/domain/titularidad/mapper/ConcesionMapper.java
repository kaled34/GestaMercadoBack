package mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.ConcesionUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.ConcesionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConcesionMapper {

    ConcesionDto toDto(ConcesionEntity entity);

    List<ConcesionDto> toDtoList(List<ConcesionEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "mercadoId", ignore = true)
    @Mapping(target = "municipioId", ignore = true)
    ConcesionEntity toEntity(ConcesionCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localId", ignore = true)
    @Mapping(target = "titularPersonaId", ignore = true)
    @Mapping(target = "fechaInicio", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "esTemporalFeria", ignore = true)
    @Mapping(target = "mercadoId", ignore = true)
    @Mapping(target = "municipioId", ignore = true)
    void updateEntityFromDto(ConcesionUpdateDto dto, @MappingTarget ConcesionEntity entity);
}
