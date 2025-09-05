package co.com.pragma.usecase.loantype;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeUseCaseTest {

    @Mock
    private LoanTypeGateway loanTypeGateway;

    @InjectMocks
    private LoanTypeUseCase useCase;

    private List<LoanType> loanTypesList;

    @BeforeEach
    void setUp() {
        loanTypesList = Arrays.asList(
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
        when(loanTypeGateway.getAllLoanTypes()).thenReturn(Flux.fromIterable(loanTypesList));

        Flux<LoanType> result = useCase.getAllLoanTypes();

        StepVerifier.create(result)
                .expectNextCount(loanTypesList.size())
                .verifyComplete();

        verify(loanTypeGateway).getAllLoanTypes();
    }

    @Test
    void validateLoanTypeExists_WithExistingType_ShouldReturnTrue() {
        when(loanTypeGateway.existById(1L)).thenReturn(Mono.just(true));

        Mono<Boolean> result = useCase.validateLoanTypeExists(1L);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(loanTypeGateway).existById(1L);
    }

    @Test
    void validateLoanTypeExists_WithNonExistingType_ShouldReturnFalse() {
        when(loanTypeGateway.existById(999L)).thenReturn(Mono.just(false));

        Mono<Boolean> result = useCase.validateLoanTypeExists(999L);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(loanTypeGateway).existById(999L);
    }
}
