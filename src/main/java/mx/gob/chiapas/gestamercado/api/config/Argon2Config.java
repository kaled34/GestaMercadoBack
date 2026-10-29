package mx.gob.chiapas.gestamercado.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Argon2Config {

    private final Argon2Properties properties;

    public Argon2Config(Argon2Properties properties) {
        this.properties = properties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                properties.getSaltLength(),
                properties.getHashLength(),
                properties.getParallelism(),
                properties.getMemory(),
                properties.getIterations()
        );
    }
}
