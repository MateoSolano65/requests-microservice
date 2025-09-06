package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.entities.LoanApplicationData;
import co.com.pragma.r2dbc.repository.LoanApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationAdapterTest {

    @Mock
    private LoanApplicationRepository repository;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private LoanApplicationAdapter adapter;

    @Captor
    private ArgumentCaptor<LoanApplicationData> dataCaptor;

    static class LoanApplicationBuilder {
        private Long idLoan = null;
        private BigDecimal loanAmount = new BigDecimal("5000000.00");
        private Long termInMonths = 24L;
        private String documentNumber = "1098765432";
        private String email = "test@example.com";
        private Long loanType = 1L;
        private Long loanStatus = 1L;

        LoanApplication build() {
            return LoanApplication.builder()
                    .idLoan(idLoan)
                    .loanAmount(loanAmount)
                    .termInMonths(termInMonths)
                    .documentNumber(documentNumber)
                    .email(email)
                    .loanType(loanType)
                    .loanStatus(loanStatus)
                    .build();
        }
    }

    static class LoanApplicationDataBuilder {
        private Long idLoan = null;
        private BigDecimal loanAmount = new BigDecimal("5000000.00");
        private Long termInMonths = 24L;
        private String documentNumber = "1098765432";
        private String email = "test@example.com";
        private Long loanType = 1L;
        private Long loanStatus = 1L;

        LoanApplicationData build() {
            return LoanApplicationData.builder()
                    .idLoan(idLoan)
                    .loanAmount(loanAmount)
                    .termInMonths(termInMonths)
                    .documentNumber(documentNumber)
                    .email(email)
                    .loanType(loanType)
                    .loanStatus(loanStatus)
                    .build();
        }
    }

    private final LoanApplicationBuilder domainBuilder = new LoanApplicationBuilder();
    private final LoanApplicationDataBuilder dataBuilder = new LoanApplicationDataBuilder();

    @Test
    @DisplayName("saveLoan(): guarda y retorna la entidad mapeada (AAA)")
    void saveLoan_ShouldSaveAndReturnLoanApplication() {
        LoanApplication input = domainBuilder.build();
        LoanApplicationData toPersist = dataBuilder.build();
        LoanApplicationData persisted = toPersist.toBuilder().idLoan(1L).build();
        LoanApplication expected = input.toBuilder().idLoan(1L).build();

        given(mapper.map(eq(input), eq(LoanApplicationData.class))).willReturn(toPersist);
        given(repository.save(eq(toPersist))).willReturn(Mono.just(persisted));
        given(mapper.map(eq(persisted), eq(LoanApplication.class))).willReturn(expected);
        given(transactionalOperator.transactional(any(Mono.class))).willAnswer(inv -> inv.getArgument(0));

        Mono<LoanApplication> result = adapter.saveLoan(input);

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();

        then(mapper).should().map(input, LoanApplicationData.class);
        then(repository).should().save(toPersist);
        then(mapper).should().map(persisted, LoanApplication.class);
        then(transactionalOperator).should().transactional(any(Mono.class));
    }

    @Test
    @DisplayName("saveLoan(): mapea correctamente los campos (AAA)")
    void saveLoan_ShouldMapFieldsCorrectly() {
        LoanApplication input = domainBuilder.build();
        LoanApplicationData toPersist = dataBuilder.build();
        LoanApplicationData persisted = toPersist.toBuilder().idLoan(1L).build();
        LoanApplication expected = input.toBuilder().idLoan(1L).build();

        given(mapper.map(eq(input), eq(LoanApplicationData.class))).willReturn(toPersist);
        given(repository.save(any(LoanApplicationData.class))).willReturn(Mono.just(persisted));
        given(mapper.map(eq(persisted), eq(LoanApplication.class))).willReturn(expected);
        given(transactionalOperator.transactional(any(Mono.class))).willAnswer(inv -> inv.getArgument(0));

        adapter.saveLoan(input).block();
        then(repository).should().save(dataCaptor.capture());
        LoanApplicationData captured = dataCaptor.getValue();

        assertThat(captured.getLoanAmount()).isEqualTo(input.getLoanAmount());
        assertThat(captured.getTermInMonths()).isEqualTo(input.getTermInMonths());
        assertThat(captured.getDocumentNumber()).isEqualTo(input.getDocumentNumber());
        assertThat(captured.getEmail()).isEqualTo(input.getEmail());
        assertThat(captured.getLoanType()).isEqualTo(input.getLoanType());
        assertThat(captured.getLoanStatus()).isEqualTo(input.getLoanStatus());
    }

    @Test
    @DisplayName("saveLoan(): propaga el error del repositorio (AAA)")
    void saveLoan_WhenRepositoryFails_ShouldPropagateError() {
        LoanApplication input = domainBuilder.build();
        LoanApplicationData toPersist = dataBuilder.build();
        RuntimeException expectedError = new RuntimeException("DB error");

        given(mapper.map(eq(input), eq(LoanApplicationData.class))).willReturn(toPersist);
        given(repository.save(eq(toPersist))).willReturn(Mono.error(expectedError));
        given(transactionalOperator.transactional(any(Mono.class))).willAnswer(inv -> inv.getArgument(0));

        Mono<LoanApplication> result = adapter.saveLoan(input);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e == expectedError)
                .verify();
    }
}
