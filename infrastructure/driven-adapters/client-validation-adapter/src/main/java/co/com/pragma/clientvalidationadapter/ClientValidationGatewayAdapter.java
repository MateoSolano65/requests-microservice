package co.com.pragma.clientvalidationadapter;

import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ClientValidationGatewayAdapter implements ClientValidationGateway {
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(true);
    }
}
