package com.project.quartz.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by user on 5:34 21/04/2025, 2025
 */

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI(@Value("${openapi.dev-url}") String devUrl,
                                 @Value("${openapi.prod-url}") String prodUrl) {

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact myContact = new Contact();
        myContact.setName("Fikri");
        myContact.setEmail("muhamadnurulfikri@gmail.com");

        License mitLicense = new License().name("Apache 2.0").url("http://springdoc.org");

        Info info = new Info()
                .title("Quartz scheduler application API")
                .version("1.0")
                .contact(myContact)
                .termsOfService("http://swagger.io/terms/")
                .description("This API exposes endpoints to manage tutorials.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info).servers(List.of(devServer, prodServer));
    }
}
