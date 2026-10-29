package mx.gob.chiapas.gestamercado.api.domain.catalogos.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public class CatalogosJdbcRepository {

    private final JdbcClient jdbcClient;

    public CatalogosJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Resuelve el valor oficial de la UMA aplicable a un mercado y fecha determinada
     * invocando gesta.fn_valor_uma(p_fecha, p_mercado).
     */
    public BigDecimal obtenerValorUma(LocalDate fecha, Long mercadoId) {
        return jdbcClient.sql("""
                SELECT gesta.fn_valor_uma(:fecha, :mercadoId)
                """)
                .param("fecha", fecha)
                .param("mercadoId", mercadoId)
                .query(BigDecimal.class)
                .single();
    }

    /**
     * Resuelve el valor de UMA sugerido para una fecha invocando gesta.fn_uma_sugerida(p_fecha).
     */
    public BigDecimal obtenerUmaSugerida(LocalDate fecha) {
        return jdbcClient.sql("""
                SELECT gesta.fn_uma_sugerida(:fecha)
                """)
                .param("fecha", fecha)
                .query(BigDecimal.class)
                .single();
    }
}
