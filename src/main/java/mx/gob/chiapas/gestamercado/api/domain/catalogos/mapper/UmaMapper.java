package mx.gob.chiapas.gestamercado.api.domain.catalogos.mapper;

import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaMercadoDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.dto.UmaOficialDto;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.UmaMercadoEntity;
import mx.gob.chiapas.gestamercado.api.domain.catalogos.model.UmaOficialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UmaMapper {

    @Mapping(target = "mercadoId", source = "id.mercadoId")
    @Mapping(target = "vigenteDesde", source = "id.vigenteDesde")
    UmaMercadoDto toDto(UmaMercadoEntity entity);

    List<UmaMercadoDto> toMercadoDtoList(List<UmaMercadoEntity> entities);

    UmaOficialDto toDto(UmaOficialEntity entity);

    List<UmaOficialDto> toOficialDtoList(List<UmaOficialEntity> entities);
}
