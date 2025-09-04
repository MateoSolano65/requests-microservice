package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationGateway loanApplicationGateway;
    private final ClientValidationGateway clientValidationGateway;
    private final LoanTypeGateway loanTypeGateway;

    public Mono<LoanApplication> create(LoanApplication loanApplication) {
        return clientValidationGateway.validateUserByEmailAndDocument(
                loanApplication.getEmail(), 
                loanApplication.getDocumentNumber())
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return validateLoanTypeExists(loanApplication.getLoanType())
                                .then(loanApplicationGateway.saveLoan(loanApplication));
                    } else {
                        return Mono.error(new BusinessRuleViolationException(ResponseCode.CLIENT_VALIDATION_ERROR));
                    }
                });
    }
    
    private Mono<Void> validateLoanTypeExists(Long loanTypeId) {
        return loanTypeGateway.existById(loanTypeId)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new BusinessRuleViolationException(ResponseCode.LOAN_TYPE_NOT_FOUND));
                    }
                });
    }
}
