package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.entities.LoanApplicationData;
import co.com.pragma.r2dbc.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationAdapterTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private LoanApplicationAdapter adapter;

    @Captor
    private ArgumentCaptor<LoanApplicationData> dataCaptor;

    private LoanApplication loanApplication;
    private LoanApplicationData loanApplicationData;
    private LoanApplicationData savedLoanApplicationData;
    private LoanApplication savedLoanApplication;

    @BeforeEach
    void setUp() {
        adapter = new LoanApplicationAdapter(repository, mapper, transactionalOperator);

        loanApplication = LoanApplication.builder()
                .loanAmount(new BigDecimal("5000000.00"))
                .termInMonths(24L)
                .documentNumber("1098765432")
                .email("test@example.com")
                .loanType(1L)
                .loanStatus(1L)
                .build();

        loanApplicationData = LoanApplicationData.builder()
                .loanAmount(new BigDecimal("5000000.00"))
                .termInMonths(24L)
                .documentNumber("1098765432")
                .email("test@example.com")
                .loanType(1L)
                .loanStatus(1L)
                .build();

        savedLoanApplicationData = loanApplicationData.toBuilder()
                .idLoan(1L)
                .build();

        savedLoanApplication = loanApplication.toBuilder()
                .idLoan(1L)
                .build();
        
        when(transactionalOperator.transactional(Mono.empty())).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void saveLoan_ShouldSaveAndReturnLoanApplication() {
        when(mapper.map(loanApplication, LoanApplicationData.class)).thenReturn(loanApplicationData);
        when(repository.save(loanApplicationData)).thenReturn(Mono.just(savedLoanApplicationData));
        when(mapper.map(savedLoanApplicationData, LoanApplication.class)).thenReturn(savedLoanApplication);

        Mono<LoanApplication> result = adapter.saveLoan(loanApplication);
        StepVerifier.create(result)
                .expectNext(savedLoanApplication)
                .verifyComplete();

        verify(mapper).map(loanApplication, LoanApplicationData.class);
        verify(repository).save(loanApplicationData);
        verify(mapper).map(savedLoanApplicationData, LoanApplication.class);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void saveLoan_ShouldMapFieldsCorrectly() {
        when(mapper.map(eq(loanApplication), eq(LoanApplicationData.class))).thenReturn(loanApplicationData);
        when(repository.save(any(LoanApplicationData.class))).thenReturn(Mono.just(savedLoanApplicationData));
        when(mapper.map(eq(savedLoanApplicationData), eq(LoanApplication.class))).thenReturn(savedLoanApplication);

        adapter.saveLoan(loanApplication).block();
        verify(repository).save(dataCaptor.capture());
        LoanApplicationData capturedData = dataCaptor.getValue();
        
        assertThat(capturedData.getLoanAmount()).isEqualTo(loanApplication.getLoanAmount());
        assertThat(capturedData.getTermInMonths()).isEqualTo(loanApplication.getTermInMonths());
        assertThat(capturedData.getDocumentNumber()).isEqualTo(loanApplication.getDocumentNumber());
        assertThat(capturedData.getEmail()).isEqualTo(loanApplication.getEmail());
        assertThat(capturedData.getLoanType()).isEqualTo(loanApplication.getLoanType());
        assertThat(capturedData.getLoanStatus()).isEqualTo(loanApplication.getLoanStatus());
    }

    @Test
    void saveLoan_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException expectedException = new RuntimeException("Database error");
        when(mapper.map(loanApplication, LoanApplicationData.class)).thenReturn(loanApplicationData);
        when(repository.save(loanApplicationData)).thenReturn(Mono.error(expectedException));

        Mono<LoanApplication> result = adapter.saveLoan(loanApplication);
        StepVerifier.create(result)
                .expectErrorMatches(error -> error == expectedException)
                .verify();
    }
}
