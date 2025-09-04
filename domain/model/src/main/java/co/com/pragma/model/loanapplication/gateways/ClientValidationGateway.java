package co.com.pragma.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface ClientValidationGateway {
    Mono<Boolean> validateUserByEmailAndDocument(String email, String document);
}
