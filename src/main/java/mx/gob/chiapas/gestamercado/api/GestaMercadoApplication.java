package mx.gob.chiapas.gestamercado.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAspectJAutoProxy
public class GestaMercadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaMercadoApplication.class, args);
    }
}
