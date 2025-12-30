package es.nicolas.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuración de Swagger para la documentación de la API
@Configuration
public class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    // Configuración del JWT
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Gestión de Alumnos con Spring Boot")
                                .version("1.0.0")
                                .description("API de ejemplo del curso Desarrollo de un API REST con Spring Boot para 2º DAW. 2025/2026")
                                .termsOfService("https://nicolas.dev/docs/license")
                                .license(
                                        new License()
                                                .name("CC BY-NC-SA 4.0")
                                                .url("https://nicolas.dev/docs/license")
                                )
                                .contact(
                                        new Contact()
                                                .name("Nicolas Developer")
                                                .email("giorgionicolasosoriobautista@gmail.com")
                                                .url("https://nicolas.dev")
                                )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Documentación del proyecto")
                                .url("https://github.com/GNicolas21/AlumnoCRUD")
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del proyecto")
                                .url("https://github.com/GNicolas21/AlumnoCRUD")
                )
                // Añadimos el esquema de seguridad para JWT
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("http")
                // .pathsToMatch("/v1/**") , esto sería todas las rutas
                .pathsToMatch("/api/" + apiVersion + "/alumnos/**") // Solo alumnos
                .displayName("API Gestión de Alumnos Spring Boot DAW 2025/2026")
                .build();
    }
}
