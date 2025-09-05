package co.com.pragma.usecase.loanstatus;

import co.com.pragma.model.loanstatus.gateways.LoanStatusGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStatusUseCaseTest {

    @Mock
    private LoanStatusGateway loanStatusGateway;

    @InjectMocks
    private LoanStatusUseCase useCase;

    @Test
    void getPendingReviewStatusId_ShouldReturnCorrectId() {
        Long expectedStatusId = 1L;
        when(loanStatusGateway.getIdByName("Pendiente")).thenReturn(Mono.just(expectedStatusId));

        Mono<Long> result = useCase.getPendingReviewStatusId();

        StepVerifier.create(result)
                .expectNext(expectedStatusId)
                .verifyComplete();

        verify(loanStatusGateway).getIdByName("Pendiente");
    }

    @Test
    void getPendingReviewStatusId_WhenStatusNotFound_ShouldReturnEmpty() {
        when(loanStatusGateway.getIdByName("Pendiente")).thenReturn(Mono.empty());

        Mono<Long> result = useCase.getPendingReviewStatusId();

        StepVerifier.create(result)
                .verifyComplete();

        verify(loanStatusGateway).getIdByName("Pendiente");
    }
}
