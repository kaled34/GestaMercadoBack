package mx.gob.chiapas.gestamercado.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "gesta.security.jwt")
public class JwtProperties {

    @NotBlank(message = "El secreto JWT no puede estar vacío")
    private String secret;

    @Positive(message = "La expiración del access token debe ser mayor a 0")
    private long accessTokenExpirationMs = 1800000; // 30 minutos por defecto

    @Positive(message = "La expiración del refresh token debe ser mayor a 0")
    private long refreshTokenExpirationMs = 604800000; // 7 días por defecto

    private String issuer = "GestaMercado-Gobierno-Chiapas";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) {
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
