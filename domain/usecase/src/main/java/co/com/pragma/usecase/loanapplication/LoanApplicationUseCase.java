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
        return Mono.just(loanApplication)
                .filterWhen(loanApplicationDomain -> clientValidationGateway.validateUserByEmailAndDocument(loanApplicationDomain.getEmail(), loanApplicationDomain.getDocumentNumber()))
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException(ResponseCode.CLIENT_VALIDATION_ERROR)))
                .filterWhen(loanApplicationDomain -> loanTypeGateway.existById(loanApplicationDomain.getLoanType()))
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException(ResponseCode.LOAN_TYPE_NOT_FOUND)))
                .flatMap(loanApplicationGateway::saveLoan);
    }

}
