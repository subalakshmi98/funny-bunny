package com.scheduling.maplewood.Config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

    /**
     * Creates an OpenAPI object for the application.
     * @return the OpenAPI object
     */
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Master Schedule API")
                        .version("1.0.0")
                        .description("API for schedule generation and management.")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("you@example.com")));
    }
}

