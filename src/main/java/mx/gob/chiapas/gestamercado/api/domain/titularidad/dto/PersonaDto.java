package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.TipoPersona;

import java.time.Instant;
import java.util.UUID;

public record PersonaDto(
        UUID id,
        TipoPersona tipoPersona,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String razonSocial,
        String curp,
        String rfc,
        String telefono,
        String correo,
        String domicilio,
        Long mercadoAltaId,
        Instant avisoPrivacidadEn,
        Instant creadoEn
) {
    public String nombreCompleto() {
        if (tipoPersona == TipoPersona.MORAL && razonSocial != null && !razonSocial.isBlank()) {
            return razonSocial;
        }
        StringBuilder sb = new StringBuilder();
        if (nombres != null) sb.append(nombres);
        if (apellidoPaterno != null) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(apellidoPaterno);
        }
        if (apellidoMaterno != null) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(apellidoMaterno);
        }
        return sb.toString();
    }
}
