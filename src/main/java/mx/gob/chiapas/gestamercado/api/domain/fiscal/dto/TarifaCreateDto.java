package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import mx.gob.chiapas.gestamercado.api.domain.enums.FuenteTarifa;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarifaCreateDto(
        @NotNull(message = "El municipio es obligatorio")
        Long municipioId,

        Long mercadoId,
        Long zonaId,
        TipoEspacio tipoEspacio,
        Long giroId,

        @NotNull(message = "El método de cálculo es obligatorio")
        MetodoCalculo metodoCalculo,

        @NotNull(message = "El valor tarifario es obligatorio")
        @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
        BigDecimal valor,

        @NotNull(message = "La unidad de valor es obligatoria")
        UnidadValor unidadValor,

        Boolean valorBaseMensual,
        BigDecimal metrosLibresBase,
        BigDecimal cuotaBase,
        Boolean incluirEnLote,

        @NotNull(message = "La periodicidad es obligatoria")
        Periodicidad periodicidad,

        BigDecimal cuotaMinima,
        BigDecimal cuotaMaxima,

        @NotNull(message = "La fuente normativa de la tarifa es obligatoria")
        FuenteTarifa fuente,

        String fundamentoLegal,

        @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
        LocalDate vigenteDesde,

        LocalDate vigenteHasta,
        String formulaCuotaDiaria
) {}
