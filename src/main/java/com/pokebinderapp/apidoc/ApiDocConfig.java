package com.pokebinderapp.apidoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
 * While the server is running, this class automatically detects the project's
 * controller classes and annotations to build API documentation
 * at the following URL: http://www.localhost:8080/swagger-ui/index.html
 *
 * Add the @Hidden annotation above any class or controller method to
 * omit documentation for one or more endpoints
 *
 * The documentation for this feature is here: https://swagger.io/specification/
 */
@Configuration
public class ApiDocConfig {

    @Bean
    public OpenAPI MyApi() {
        final String apiTitle = "POKE-API";
        final String securitySchemeName = "bearerAuth";

        // Swagger docs
        // https://swagger.io/docs/specification/authentication/bearer-authentication/

        final SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .servers(List.of(new Server().url("/")))
                .info(new Info().title(apiTitle)
                        .description("REST API for Jordan's Pokémon TCG Binder Web App.")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components( new Components().addSecuritySchemes(securitySchemeName, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
