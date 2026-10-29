package mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.CorteCajaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.CorteCajaDiarioEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CorteCajaMapper {

    CorteCajaDto toDto(CorteCajaDiarioEntity entity);

    List<CorteCajaDto> toDtoList(List<CorteCajaDiarioEntity> entities);
}
