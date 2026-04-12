package com.example.simplifyStorePrime.config;

import com.example.simplifyStorePrime.commons.AppConstants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(AppConstants.API_TITLE)
                        .description(AppConstants.API_DESCRIPTION)
                        .version(AppConstants.API_VERSION)
                        .contact(new Contact()
                                .name(AppConstants.API_AUTHOR)
                        ))
                .addSecurityItem(new SecurityRequirement().addList(AppConstants.BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(AppConstants.BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(AppConstants.BEARER_SCHEME)
                                        .bearerFormat(AppConstants.BEARER_FORMAT)
                        ));
    }
}