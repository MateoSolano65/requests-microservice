package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entities.LoanTypeData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanTypeRepository extends ReactiveCrudRepository<LoanTypeData, Long>, ReactiveQueryByExampleExecutor<LoanTypeData> {
    
}

