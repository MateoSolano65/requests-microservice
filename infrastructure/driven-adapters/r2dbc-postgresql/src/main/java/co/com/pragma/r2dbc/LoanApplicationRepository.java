package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entities.LoanApplicationData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationRepository extends ReactiveCrudRepository<LoanApplicationData, Long>, ReactiveQueryByExampleExecutor<LoanApplicationData> {

}
