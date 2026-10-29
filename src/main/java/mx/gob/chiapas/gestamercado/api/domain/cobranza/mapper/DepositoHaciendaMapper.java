package mx.gob.chiapas.gestamercado.api.domain.cobranza.mapper;

import mx.gob.chiapas.gestamercado.api.domain.cobranza.dto.DepositoHaciendaDto;
import mx.gob.chiapas.gestamercado.api.domain.cobranza.model.DepositoHaciendaEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepositoHaciendaMapper {

    DepositoHaciendaDto toDto(DepositoHaciendaEntity entity);

    List<DepositoHaciendaDto> toDtoList(List<DepositoHaciendaEntity> entities);
}
