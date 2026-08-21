package org.droneshow.booking_service.client;

import org.droneshow.booking_service.dto.PackageResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PackageServiceClient {

    private final WebClient webClient;

    public PackageServiceClient(
            WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder
                .baseUrl("http://package-service:8080")
                .build();
    }

    public PackageResponse getPackageByName(String name) {

        return webClient.get()
                .uri("/packages/name/{name}", name)
                .retrieve()
                .bodyToMono(PackageApiResponse.class)
                .map(PackageApiResponse::getData)
                .block();
    }

    private static class PackageApiResponse {

        private String message;
        private PackageResponse data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public PackageResponse getData() {
            return data;
        }

        public void setData(PackageResponse data) {
            this.data = data;
        }
    }
}
