package mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.PagoDetalleDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.PagoDetalleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoDetalleMapper {

    @Mapping(target = "pagoId", source = "id.pagoId")
    @Mapping(target = "cargoId", source = "id.cargoId")
    PagoDetalleDto toDto(PagoDetalleEntity entity);

    List<PagoDetalleDto> toDtoList(List<PagoDetalleEntity> entities);
}
