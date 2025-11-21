package com.synkronos.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI synkronosOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.synkronos.ai");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@synkronos.ai");
        contact.setName("Synkronos AI Support");

        License license = new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
            .title("Synkronos AI Job Portal API")
            .version("1.0.0")
            .contact(contact)
            .description("RESTful API for AI-Powered Job Recommendation & Talent Matching Portal")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, prodServer));
    }
}

