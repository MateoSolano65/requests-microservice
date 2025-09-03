package co.com.pragma.usecase.loantype;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeGateway loanTypeGateway;

    public Flux<LoanType> getAllLoanTypes() {
        return loanTypeGateway.getAllLoanTypes();
    }
}
