package mx.gob.chiapas.gestamercado.api.domain.acceso.mapper;

import mx.gob.chiapas.gestamercado.api.domain.acceso.dto.DispositivoResponseDto;
import mx.gob.chiapas.gestamercado.api.domain.acceso.model.DispositivoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HexFormat;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DispositivoMapper {

    @Mapping(target = "secretoHmacHex", source = "secretoHmac", qualifiedByName = "bytesToHex")
    DispositivoResponseDto toDto(DispositivoEntity entity);

    List<DispositivoResponseDto> toDtoList(List<DispositivoEntity> entities);

    @Named("bytesToHex")
    default String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return HexFormat.of().formatHex(bytes);
    }
}
