package mx.gob.chiapas.gestamercado.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GestaMercado REST API - Gobierno de Chiapas")
                        .version("1.0.0")
                        .description("API RESTful de alta seguridad para la gestión integral de mercados públicos, " +
                                "padrón georreferenciado PostGIS, recaudación fiscal, corte de caja y sincronización offline-first.")
                        .contact(new Contact()
                                .name("Dirección de Tecnologías de Información")
                                .email("soporte.mercados@chiapas.gob.mx"))
                        .license(new License().name("Uso Oficial Gubernamental")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT de acceso emitido por /api/v1/auth/login")));
    }
}
