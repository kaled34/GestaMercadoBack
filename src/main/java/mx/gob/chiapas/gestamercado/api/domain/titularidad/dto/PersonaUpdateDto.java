package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import java.time.Instant;

public record PersonaUpdateDto(
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String razonSocial,
        String curp,
        String rfc,
        String telefono,
        String correo,
        String domicilio,
        Instant avisoPrivacidadEn
) {}
