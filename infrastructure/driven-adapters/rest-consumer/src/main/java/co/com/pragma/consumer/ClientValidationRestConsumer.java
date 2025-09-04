package co.com.pragma.consumer;

import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientValidationRestConsumer implements ClientValidationGateway {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "validateUser")
    public Mono<Boolean> validateUserByEmailAndDocument(String email, String document) {
        return client
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/validate")
                .queryParam("email", email)
                .queryParam("document", document)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> "true".equalsIgnoreCase(response))
            .onErrorReturn(false);
    }
}
