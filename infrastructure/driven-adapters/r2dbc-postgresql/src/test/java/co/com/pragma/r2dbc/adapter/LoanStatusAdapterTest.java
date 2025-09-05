package co.com.pragma.r2dbc.adapter;

import co.com.pragma.r2dbc.entities.LoanStatusData;
import co.com.pragma.r2dbc.repository.LoanStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStatusAdapterTest {

    @Mock
    private LoanStatusRepository repository;

    private LoanStatusAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LoanStatusAdapter(repository);

    }

    @Test
    void getIdByName_WithExistingStatus_ShouldReturnId() {
        String statusName = "Pendiente";
        Long expectedId = 1L;
        LoanStatusData statusData = LoanStatusData.builder()
                .id(expectedId)
                .name(statusName)
                .description("Estado pendiente de revisión")
                .build();
        
        when(repository.findByName(statusName)).thenReturn(Mono.just(statusData));

        Mono<Long> result = adapter.getIdByName(statusName);
        StepVerifier.create(result)
                .expectNext(expectedId)
                .verifyComplete();

        verify(repository).findByName(statusName);
    }

    @Test
    void getIdByName_WithNonExistentStatus_ShouldReturnError() {
        String nonExistentStatus = "NoExiste";
        when(repository.findByName(nonExistentStatus)).thenReturn(Mono.empty());

        Mono<Long> result = adapter.getIdByName(nonExistentStatus);
        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                        error instanceof RuntimeException && 
                        error.getMessage().equals("Estado no encontrado: " + nonExistentStatus))
                .verify();

        verify(repository).findByName(nonExistentStatus);
    }

    @Test
    void getIdByName_WhenRepositoryFails_ShouldPropagateError() {
        String statusName = "Pendiente";
        RuntimeException expectedException = new RuntimeException("Database error");
        when(repository.findByName(statusName)).thenReturn(Mono.error(expectedException));

        Mono<Long> result = adapter.getIdByName(statusName);
        StepVerifier.create(result)
                .expectErrorMatches(error -> error == expectedException)
                .verify();

        verify(repository).findByName(statusName);
    }
}
