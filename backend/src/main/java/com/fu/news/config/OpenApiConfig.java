package com.fu.news.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI funewsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FUNewsManagementSystem — Nguyen Hung Phat Dat (CE191354)")
                .description("Tác giả: Nguyen Hung Phat Dat — CE191354")
                .version("1.0.0")
                .contact(new Contact().name("Nguyen Hung Phat Dat")));
    }
}
