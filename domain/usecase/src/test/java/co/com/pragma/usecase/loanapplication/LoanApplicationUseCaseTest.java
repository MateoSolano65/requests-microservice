package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationGateway loanApplicationGateway;

    @Mock
    private ClientValidationGateway clientValidationGateway;

    @Mock
    private LoanTypeGateway loanTypeGateway;

    @InjectMocks
    private LoanApplicationUseCase useCase;

    static class LoanApplicationTestDataBuilder {
        private Long idLoan = null;
        private String documentNumber = "1098765432";
        private String email = "test@example.com";
        private BigDecimal loanAmount = new BigDecimal("5000000");
        private Long termInMonths = 24L;
        private Long loanType = 1L;

        LoanApplicationTestDataBuilder withIdLoan(Long idLoan) {
            this.idLoan = idLoan;
            return this;
        }

        LoanApplicationTestDataBuilder withDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        LoanApplicationTestDataBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        LoanApplicationTestDataBuilder withLoanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
            return this;
        }

        LoanApplicationTestDataBuilder withTermInMonths(Long termInMonths) {
            this.termInMonths = termInMonths;
            return this;
        }

        LoanApplicationTestDataBuilder withLoanType(Long loanType) {
            this.loanType = loanType;
            return this;
        }

        LoanApplication build() {
            return LoanApplication.builder()
                    .idLoan(idLoan)
                    .documentNumber(documentNumber)
                    .email(email)
                    .loanAmount(loanAmount)
                    .termInMonths(termInMonths)
                    .loanType(loanType)
                    .build();
        }
    }

    private final LoanApplicationTestDataBuilder loanBuilder = new LoanApplicationTestDataBuilder();

    @Test
    @DisplayName("create(): OK cuando cliente válido y tipo existente (AAA)")
    void createLoanApplication_WithValidClientAndLoanType_ShouldSucceed() {
        // Arrange
        LoanApplication input = loanBuilder.build();

        given(clientValidationGateway.validateUserByEmailAndDocument(
                eq(input.getEmail()), eq(input.getDocumentNumber())))
                .willReturn(Mono.just(true));
        given(loanTypeGateway.existById(eq(input.getLoanType())))
                .willReturn(Mono.just(true));
        given(loanApplicationGateway.saveLoan(any(LoanApplication.class)))
                .willAnswer(inv -> {
                    LoanApplication in = inv.getArgument(0);
                    return Mono.just(in.toBuilder().idLoan(99L).build());
                });

        // Act
        Mono<LoanApplication> result = useCase.create(input);

        // Assert
        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertNotNull(saved.getIdLoan());
                    assertEquals(input.getDocumentNumber(), saved.getDocumentNumber());
                    assertEquals(input.getEmail(), saved.getEmail());
                    assertEquals(input.getLoanType(), saved.getLoanType());
                })
                .verifyComplete();

        then(clientValidationGateway).should()
                .validateUserByEmailAndDocument(input.getEmail(), input.getDocumentNumber());
        then(loanTypeGateway).should().existById(input.getLoanType());
        then(loanApplicationGateway).should().saveLoan(input);
        then(clientValidationGateway).shouldHaveNoMoreInteractions();
        then(loanTypeGateway).shouldHaveNoMoreInteractions();
        then(loanApplicationGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("create(): error CLIENT_VALIDATION_ERROR cuando el cliente NO es válido (AAA)")
    void createLoanApplication_WithInvalidClient_ShouldReturnError() {
        // Arrange
        LoanApplication input = loanBuilder.build();

        given(clientValidationGateway.validateUserByEmailAndDocument(
                eq(input.getEmail()), eq(input.getDocumentNumber())))
                .willReturn(Mono.just(false));

        // Act
        Mono<LoanApplication> result = useCase.create(input);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessRuleViolationException);
                    BusinessRuleViolationException ex = (BusinessRuleViolationException) error;
                    // Ajusta si tu excepción expone el enum directamente:
                    assertEquals(ResponseCode.CLIENT_VALIDATION_ERROR.getCodeValue(), ex.code());
                })
                .verify();

        then(clientValidationGateway).should()
                .validateUserByEmailAndDocument(input.getEmail(), input.getDocumentNumber());
        then(clientValidationGateway).shouldHaveNoMoreInteractions();
        // Con la versión correcta del UC, NO se llaman estas dependencias:
        then(loanTypeGateway).shouldHaveNoInteractions();
        then(loanApplicationGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("create(): error LOAN_TYPE_NOT_FOUND cuando el tipo NO existe (AAA)")
    void createLoanApplication_WithNonExistentLoanType_ShouldReturnBusinessError() {
        // Arrange
        LoanApplication input = loanBuilder.build();

        given(clientValidationGateway.validateUserByEmailAndDocument(
                eq(input.getEmail()), eq(input.getDocumentNumber())))
                .willReturn(Mono.just(true));
        given(loanTypeGateway.existById(eq(input.getLoanType())))
                .willReturn(Mono.just(false));

        // Act
        Mono<LoanApplication> result = useCase.create(input);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessRuleViolationException);
                    BusinessRuleViolationException ex = (BusinessRuleViolationException) error;
                    assertEquals(ResponseCode.LOAN_TYPE_NOT_FOUND.getCodeValue(), ex.code());
                })
                .verify();

        then(clientValidationGateway).should()
                .validateUserByEmailAndDocument(input.getEmail(), input.getDocumentNumber());
        then(loanTypeGateway).should().existById(input.getLoanType());
        // Con la versión correcta del UC, NO se intenta guardar:
        then(loanApplicationGateway).shouldHaveNoInteractions();
        then(clientValidationGateway).shouldHaveNoMoreInteractions();
        then(loanTypeGateway).shouldHaveNoMoreInteractions();
    }
}