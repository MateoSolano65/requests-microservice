package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import co.com.pragma.r2dbc.entities.LoanTypeData;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repository.LoanTypeRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeData, Long, LoanTypeRepository>
        implements LoanTypeGateway {


    public LoanTypeAdapter(LoanTypeRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    public Flux<LoanType> getAllLoanTypes() {
        return findAll();
    }
    
    @Override
    public Mono<Boolean> existById(Long id) {
        return repository.existsById(id);
    }
}

