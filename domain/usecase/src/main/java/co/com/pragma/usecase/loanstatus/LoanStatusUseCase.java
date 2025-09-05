package co.com.pragma.usecase.loanstatus;

import co.com.pragma.model.loanstatus.LoanStatus;
import co.com.pragma.model.loanstatus.gateways.LoanStatusGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanStatusUseCase {
    private final LoanStatusGateway loanStatusGateway;
 
    public Mono<Long> getPendingReviewStatusId() {
        return loanStatusGateway.getIdByName("Pendiente");
    }
}
