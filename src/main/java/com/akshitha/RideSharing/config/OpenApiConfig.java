package com.akshitha.RideSharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI rideShareApi(){
        return new OpenAPI()
        .info(
            new Info()
                .title("Ride sharing Backend API")
                .version("1.0")
                .description("Ride Sharing system")
        );
    }
}
