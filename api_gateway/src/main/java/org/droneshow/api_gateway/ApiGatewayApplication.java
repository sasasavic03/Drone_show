package org.droneshow.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        excludeName = "org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration"
)
public class ApiGatewayApplication {

    @Bean
    public org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder() {
        return org.springframework.web.reactive.function.client.WebClient.builder();
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
