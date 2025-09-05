package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entities.LoanStatusData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanStatusRepository extends ReactiveCrudRepository<LoanStatusData, Long>, ReactiveQueryByExampleExecutor<LoanStatusData> {

    Mono<LoanStatusData> findByName(String name);
}
