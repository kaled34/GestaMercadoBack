package mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper;

import mx.gob.chiapas.gestamercado.api.common.spatial.GeoJsonMapper;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GeoJsonMapper.class})
public interface PagoMapper {

    @Mapping(target = "ubicacionCobro", source = "ubicacionCobro")
    PagoDto toDto(PagoEntity entity);

    List<PagoDto> toDtoList(List<PagoEntity> entities);
}
