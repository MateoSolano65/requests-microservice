package co.com.pragma.usecase.createapplication;

import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ErrorType;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationGateway loanApplicationGateway;
    private final ClientValidationGateway clientValidationGateway;
    private final LoanTypeGateway loanTypeGateway;

    public Mono<LoanApplication> create(String token, LoanApplication loanApplication) {
        return clientValidationGateway.validateToken(token)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return validateLoanTypeExists(loanApplication.getLoanType())
                                .then(loanApplicationGateway.saveLoan(loanApplication));
                    } else {
                        return Mono.error(new RuntimeException("Token inválido"));
                    }
                });
    }
    
    private Mono<Void> validateLoanTypeExists(Long loanTypeId) {
        return loanTypeGateway.existById(loanTypeId)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new BusinessRuleViolationException(ErrorType.LOAN_TYPE_NOT_FOUND));
                    }
                });
    }
}
