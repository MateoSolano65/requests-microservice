package co.com.pragma.model.loanstatus.gateways;

import co.com.pragma.model.loanstatus.LoanStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanStatusGateway {
    Mono<Long> getIdByName(String statusName);
}
