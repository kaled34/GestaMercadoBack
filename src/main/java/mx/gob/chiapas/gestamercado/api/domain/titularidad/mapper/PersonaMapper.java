package mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.PersonaUpdateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.PersonaEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonaMapper {

    PersonaDto toDto(PersonaEntity entity);

    List<PersonaDto> toDtoList(List<PersonaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    PersonaEntity toEntity(PersonaCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoPersona", ignore = true)
    @Mapping(target = "mercadoAltaId", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    void updateEntityFromDto(PersonaUpdateDto dto, @MappingTarget PersonaEntity entity);
}
