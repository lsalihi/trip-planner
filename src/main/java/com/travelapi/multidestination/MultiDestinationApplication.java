package com.travelapi.multidestination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;

@SpringBootApplication
@EnableMongoRepositories
@OpenAPIDefinition(
    info = @Info(
        title = "Multi-Destination Travel API",
        version = "1.0",
        description = "API for planning multi-destination trips based on budget and preferences",
        contact = @Contact(name = "Travel API Team", email = "contact@travelapi.com")
    )
)
public class MultiDestinationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiDestinationApplication.class, args);
    }
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
