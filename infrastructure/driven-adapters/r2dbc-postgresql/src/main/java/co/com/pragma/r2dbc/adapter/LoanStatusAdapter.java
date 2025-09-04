package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loanstatus.gateways.LoanStatusGateway;
import co.com.pragma.r2dbc.repository.LoanStatusRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class LoanStatusAdapter implements LoanStatusGateway {

    private final LoanStatusRepository repository;

    public LoanStatusAdapter(LoanStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Long> getIdByName(String statusName) {
        return repository.findByName(statusName)
                .map(loanStatusData -> loanStatusData.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Estado no encontrado: " + statusName)));
    }
}
