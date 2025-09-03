package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import co.com.pragma.r2dbc.entities.LoanTypeData;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repository.LoanTypeRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@Repository
public class LoanTypeAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeData, Long, LoanTypeRepository>
        implements LoanTypeGateway {

    private final TransactionalOperator transactionalOperator;

    public LoanTypeAdapter(LoanTypeRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Flux<LoanType> getAllLoanTypes() {
        return repository.findAll()
                .map(data -> mapper.map(data, LoanType.class))
                .as(transactionalOperator::transactional);
    }
}

