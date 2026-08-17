package org.droneshow.api_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProxyController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final Map<String, String> backends = new HashMap<>() {{
        put("auth", "http://auth_service:3001");
        put("users", "http://user_service:3002");
        put("packages", "http://package_service:3003");
        put("bookings", "http://booking_service:3004");
        put("media", "http://media_service:3005");
    }};

    @RequestMapping({"/auth/**", "/users/**", "/packages/**", "/bookings/**", "/media/**"})
    public Mono<ResponseEntity<byte[]>> proxy(ServerHttpRequest request) {
        String rawPath = request.getURI().getRawPath();
        if (rawPath == null || rawPath.length() < 2) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        String[] parts = rawPath.split("/", 3);
        // parts[0] == "" (leading slash), parts[1] == service key
        String serviceKey = parts.length > 1 ? parts[1] : "";
        String remainder = parts.length > 2 ? "/" + parts[2] : "";

        String backend = backends.get(serviceKey);
        if (backend == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        String query = request.getURI().getRawQuery();
        String forwardUri = backend + remainder + (query != null && !query.isEmpty() ? "?" + query : "");

        HttpMethod method = request.getMethod();
        if (method == null) {
            method = HttpMethod.GET;
        }

        WebClient.RequestBodySpec spec = webClientBuilder
                .build()
                .method(method)
                .uri(URI.create(forwardUri));

        // copy headers
        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach((k, v) -> headers.put(k, v));
        // remove host header to avoid forwarding original host
        headers.remove(HttpHeaders.HOST);

        spec.headers(h -> h.addAll(headers));

        // decide whether to forward a body. For safe defaults, GET/HEAD won't send a body.
        Mono<ResponseEntity<byte[]>> respMono;
        if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) {
            respMono = spec.exchangeToMono(clientResponse -> clientResponse.toEntity(byte[].class));
        } else {
            respMono = spec
                    .body(BodyInserters.fromDataBuffers(request.getBody()))
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(byte[].class));
        }

        // map any internal errors to a 502 Bad Gateway so callers get a useful response
        return respMono.onErrorResume(ex -> {
            byte[] msg = (ex.getMessage() == null ? "" : ex.getMessage()).getBytes();
            return Mono.just(ResponseEntity.status(502).headers(new HttpHeaders()).body(msg));
        });
    }
}




