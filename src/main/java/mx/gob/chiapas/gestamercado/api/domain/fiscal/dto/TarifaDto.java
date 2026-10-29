package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import mx.gob.chiapas.gestamercado.api.domain.enums.FuenteTarifa;
import mx.gob.chiapas.gestamercado.api.domain.enums.MetodoCalculo;
import mx.gob.chiapas.gestamercado.api.domain.enums.Periodicidad;
import mx.gob.chiapas.gestamercado.api.domain.enums.TipoEspacio;
import mx.gob.chiapas.gestamercado.api.domain.enums.UnidadValor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TarifaDto(
        Long id,
        Long municipioId,
        Long mercadoId,
        Long zonaId,
        TipoEspacio tipoEspacio,
        Long giroId,
        MetodoCalculo metodoCalculo,
        BigDecimal valor,
        UnidadValor unidadValor,
        Boolean valorBaseMensual,
        BigDecimal metrosLibresBase,
        BigDecimal cuotaBase,
        Boolean incluirEnLote,
        Periodicidad periodicidad,
        BigDecimal cuotaMinima,
        BigDecimal cuotaMaxima,
        FuenteTarifa fuente,
        String fundamentoLegal,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta,
        Boolean activo,
        Instant creadoEn,
        String formulaCuotaDiaria
) {}
