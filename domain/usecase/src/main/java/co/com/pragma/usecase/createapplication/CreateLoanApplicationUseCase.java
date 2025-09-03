package co.com.pragma.usecase.createapplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationGateway loanApplicationGateway;

    public Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication) {
        return (loanApplicationGateway.saveLoan(loanApplication));
    }
}
