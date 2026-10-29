package mx.gob.chiapas.gestamercado.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "gesta.security.rls")
public class RlsProperties {

    @NotBlank(message = "El secreto HMAC para RLS no puede estar vacío")
    @Pattern(regexp = "^[0-9a-fA-F]{64}$", message = "El secreto HMAC debe ser una cadena hexadecimal de 64 caracteres (32 bytes)")
    private String hmacSecretHex;

    @NotNull(message = "El UUID del usuario de sistema es obligatorio")
    private UUID systemUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private boolean enabled = true;

    public String getHmacSecretHex() {
        return hmacSecretHex;
    }

    public void setHmacSecretHex(String hmacSecretHex) {
        this.hmacSecretHex = hmacSecretHex;
    }

    public UUID getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(UUID systemUserId) {
        this.systemUserId = systemUserId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
