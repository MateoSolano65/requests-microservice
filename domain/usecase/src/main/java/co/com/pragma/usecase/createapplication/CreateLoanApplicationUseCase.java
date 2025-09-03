package co.com.pragma.usecase.createapplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationGateway loanApplicationGateway;
    private final ClientValidationGateway clientValidationGateway;

    public Mono<LoanApplication> createLoanApplication(String token, LoanApplication loanApplication) {
        return clientValidationGateway.validateToken(token)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return loanApplicationGateway.saveLoan(loanApplication);
                    } else {
                        return Mono.error(new RuntimeException("Token inválido"));
                    }
                });
    }
}
