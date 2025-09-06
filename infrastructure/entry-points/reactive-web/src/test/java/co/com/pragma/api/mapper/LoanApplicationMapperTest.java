package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.loanstatus.LoanStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoanApplicationMapperTest {

    @Mock
    private LoanStatusUseCase loanStatusUseCase;

    private LoanApplicationMapper mapper;

    private final DtoBuilder dtoBuilder = new DtoBuilder();
    private final DomainBuilder domainBuilder = new DomainBuilder();

    @BeforeEach
    void setUp() {
        mapper = new LoanApplicationMapper(loanStatusUseCase);
    }

    @Test
    void toLoanApplicationWithPendingStatus_mapsAllFields_andSetsPendingStatus() {
        LoanApplicationDTO dto = dtoBuilder.withIdLoan(null).withLoanStatus(null).build();
        Long pendingId = 9L;
        given(loanStatusUseCase.getPendingReviewStatusId()).willReturn(Mono.just(pendingId));

        Mono<LoanApplication> result = mapper.toLoanApplicationWithPendingStatus(dto);

        StepVerifier.create(result)
                .assertNext(domain -> {
                    assertThat(domain.getIdLoan()).isNull();
                    assertThat(domain.getLoanAmount()).isEqualTo(dto.getLoanAmount());
                    assertThat(domain.getTermInMonths()).isEqualTo(dto.getTermInMonths());
                    assertThat(domain.getDocumentNumber()).isEqualTo(dto.getDocumentNumber());
                    assertThat(domain.getEmail()).isEqualTo(dto.getEmail());
                    assertThat(domain.getLoanType()).isEqualTo(dto.getLoanType());
                    assertThat(domain.getLoanStatus()).isEqualTo(pendingId);
                })
                .verifyComplete();

        then(loanStatusUseCase).should().getPendingReviewStatusId();
    }

    @Test
    void toLoanApplicationWithPendingStatus_propagatesErrorFromUseCase() {
        LoanApplicationDTO dto = dtoBuilder.build();
        RuntimeException boom = new RuntimeException("cant resolve status");
        given(loanStatusUseCase.getPendingReviewStatusId()).willReturn(Mono.error(boom));

        StepVerifier.create(mapper.toLoanApplicationWithPendingStatus(dto))
                .expectErrorMatches(e -> e == boom)
                .verify();

        then(loanStatusUseCase).should().getPendingReviewStatusId();
    }

    @Test
    void toLoanApplicationDTO_mapsAllFields() {
        LoanApplication domain = domainBuilder.build();

        LoanApplicationDTO dto = mapper.toLoanApplicationDTO(domain);

        assertThat(dto.getIdLoan()).isEqualTo(domain.getIdLoan());
        assertThat(dto.getLoanAmount()).isEqualTo(domain.getLoanAmount());
        assertThat(dto.getTermInMonths()).isEqualTo(domain.getTermInMonths());
        assertThat(dto.getDocumentNumber()).isEqualTo(domain.getDocumentNumber());
        assertThat(dto.getEmail()).isEqualTo(domain.getEmail());
        assertThat(dto.getLoanType()).isEqualTo(domain.getLoanType());
        assertThat(dto.getLoanStatus()).isEqualTo(domain.getLoanStatus());
    }

    static class DtoBuilder {
        private Long idLoan = 1L;
        private BigDecimal loanAmount = new BigDecimal("1500000.00");
        private Long termInMonths = 12L;
        private String documentNumber = "1234567890";
        private String email = "user@test.com";
        private Long loanType = 2L;
        private Long loanStatus = 5L;

        DtoBuilder withIdLoan(Long v) { this.idLoan = v; return this; }
        DtoBuilder withLoanStatus(Long v) { this.loanStatus = v; return this; }

        LoanApplicationDTO build() {
            return LoanApplicationDTO.builder()
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

    static class DomainBuilder {
        private Long idLoan = 7L;
        private BigDecimal loanAmount = new BigDecimal("2500000.00");
        private Long termInMonths = 24L;
        private String documentNumber = "1098765432";
        private String email = "another@test.com";
        private Long loanType = 3L;
        private Long loanStatus = 8L;

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
}
