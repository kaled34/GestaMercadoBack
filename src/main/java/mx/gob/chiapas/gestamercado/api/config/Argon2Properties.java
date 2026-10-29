package mx.gob.chiapas.gestamercado.api.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "gesta.security.argon2")
public class Argon2Properties {

    @Positive(message = "La longitud del salt debe ser positiva")
    private int saltLength = 16;

    @Positive(message = "La longitud del hash debe ser positiva")
    private int hashLength = 32;

    @Positive(message = "El paralelismo debe ser positivo")
    private int parallelism = 1;

    @Positive(message = "La memoria debe ser positiva")
    private int memory = 65536; // 64 MB en KB

    @Positive(message = "El número de iteraciones debe ser positivo")
    private int iterations = 3;

    public int getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public int getHashLength() {
        return hashLength;
    }

    public void setHashLength(int hashLength) {
        this.hashLength = hashLength;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
