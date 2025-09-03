package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.r2dbc.entities.LoanApplicationData;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repository.LoanApplicationRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class LoanApplicationAdapter extends ReactiveAdapterOperations<LoanApplication, LoanApplicationData, Long, LoanApplicationRepository> implements LoanApplicationGateway {

    private final TransactionalOperator transactionalOperator;

    public LoanApplicationAdapter(LoanApplicationRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanApplication> saveLoan(LoanApplication loanApplication) {
        return repository.save(mapper.map(loanApplication, LoanApplicationData.class))
                .map(data -> mapper.map(data, LoanApplication.class))
                .as(transactionalOperator::transactional);
    }

}
