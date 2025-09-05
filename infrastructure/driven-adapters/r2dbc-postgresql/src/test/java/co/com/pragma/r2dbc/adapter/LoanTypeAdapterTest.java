package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entities.LoanTypeData;
import co.com.pragma.r2dbc.repository.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeAdapterTest {

    @Mock
    private LoanTypeRepository repository;

    @Mock
    private ObjectMapper mapper;

    private LoanTypeAdapter adapter;

    private List<LoanTypeData> loanTypesData;
    private List<LoanType> loanTypes;

    @BeforeEach
    void setUp() {
        adapter = new LoanTypeAdapter(repository, mapper);

        loanTypesData = Arrays.asList(
            LoanTypeData.builder()
                .id(1L)
                .name("Préstamo Personal")
                .minAmount(new BigDecimal("1000000"))
                .maxAmount(new BigDecimal("50000000"))
                .interestRate(new BigDecimal("0.12"))
                .automaticValidation(true)
                .build(),
            LoanTypeData.builder()
                .id(2L)
                .name("Préstamo Hipotecario")
                .minAmount(new BigDecimal("50000000"))
                .maxAmount(new BigDecimal("500000000"))
                .interestRate(new BigDecimal("0.08"))
                .automaticValidation(false)
                .build()
        );

        loanTypes = Arrays.asList(
            LoanType.builder()
                .id(1L)
                .name("Préstamo Personal")
                .minAmount(new BigDecimal("1000000"))
                .maxAmount(new BigDecimal("50000000"))
                .interestRate(new BigDecimal("0.12"))
                .automaticValidation(true)
                .build(),
            LoanType.builder()
                .id(2L)
                .name("Préstamo Hipotecario")
                .minAmount(new BigDecimal("50000000"))
                .maxAmount(new BigDecimal("500000000"))
                .interestRate(new BigDecimal("0.08"))
                .automaticValidation(false)
                .build()
        );
    }

    @Test
    void getAllLoanTypes_ShouldReturnAllTypes() {
        when(repository.findAll()).thenReturn(Flux.fromIterable(loanTypesData));
        when(mapper.map(loanTypesData.get(0), LoanType.class)).thenReturn(loanTypes.get(0));
        when(mapper.map(loanTypesData.get(1), LoanType.class)).thenReturn(loanTypes.get(1));

        Flux<LoanType> result = adapter.getAllLoanTypes();
        StepVerifier.create(result)
                .expectNext(loanTypes.get(0))
                .expectNext(loanTypes.get(1))
                .verifyComplete();

        verify(repository).findAll();
        verify(mapper).map(loanTypesData.get(0), LoanType.class);
        verify(mapper).map(loanTypesData.get(1), LoanType.class);
    }

    @Test
    void existById_WhenExists_ShouldReturnTrue() {
        when(repository.existsById(1L)).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existById(1L);
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(1L);
    }

    @Test
    void existById_WhenNotExists_ShouldReturnFalse() {
        when(repository.existsById(999L)).thenReturn(Mono.just(false));

        Mono<Boolean> result = adapter.existById(999L);
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsById(999L);
    }

    @Test
    void getAllLoanTypes_WhenNoTypes_ShouldReturnEmptyFlux() {
        when(repository.findAll()).thenReturn(Flux.empty());

        Flux<LoanType> result = adapter.getAllLoanTypes();
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getAllLoanTypes_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException expectedException = new RuntimeException("Database error");
        when(repository.findAll()).thenReturn(Flux.error(expectedException));

        Flux<LoanType> result = adapter.getAllLoanTypes();
        StepVerifier.create(result)
                .expectErrorMatches(error -> error == expectedException)
                .verify();
    }
}
