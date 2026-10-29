package mx.gob.chiapas.gestamercado.api.domain.fiscal.mapper;

import mx.gob.chiapas.gestamercado.api.domain.fiscal.dto.CargoDto;
import mx.gob.chiapas.gestamercado.api.domain.fiscal.model.CargoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CargoMapper {

    CargoDto toDto(CargoEntity entity);

    List<CargoDto> toDtoList(List<CargoEntity> entities);
}
