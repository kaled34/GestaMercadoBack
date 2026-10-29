package mx.gob.chiapas.gestamercado.api.domain.titularidad.mapper;

import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoCreateDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.dto.TraspasoDto;
import mx.gob.chiapas.gestamercado.api.domain.titularidad.model.TraspasoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraspasoMapper {

    TraspasoDto toDto(TraspasoEntity entity);

    List<TraspasoDto> toDtoList(List<TraspasoEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "concesionDestinoId", ignore = true)
    @Mapping(target = "fechaEfectiva", ignore = true)
    @Mapping(target = "numeroExpediente", ignore = true)
    @Mapping(target = "dictamen", ignore = true)
    @Mapping(target = "fechaDictamen", ignore = true)
    @Mapping(target = "notificadoAFiscalEn", ignore = true)
    @Mapping(target = "regularizado", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "cargoTraspasoId", ignore = true)
    TraspasoEntity toEntity(TraspasoCreateDto dto);
}
