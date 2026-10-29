package mx.gob.chiapas.gestamercado.api.domain.titularidad.dto;

import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoPersona;

import java.time.Instant;

public record PersonaCreateDto(
        @NotNull(message = "El tipo de persona es obligatorio (FISICA o MORAL)")
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

        @NotNull(message = "El ID del mercado de alta es obligatorio")
        Long mercadoAltaId,

        Instant avisoPrivacidadEn
) {}
