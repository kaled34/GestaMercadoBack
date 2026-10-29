package mx.gob.chiapas.gestamercado.api.domain.fiscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReglaMoraDto(
        Long id,
        Long municipioId,
        Long mercadoId,
        BigDecimal porcentajeRecargo,
        Short diasGracia,
        String fundamentoLegal,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta,
        Boolean activo,
        String tipoCalculo,
        BigDecimal topeMaximoPct
) {}
