package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .documentNumber("1098765432")
                .email("test@example.com")
                .loanAmount(new BigDecimal("5000000"))
                .termInMonths(24L)
                .loanType(1L)
                .build();
    }

    @Test
    void createLoanApplication_WithValidClientAndLoanType_ShouldSucceed() {
        when(clientValidationGateway.validateUserByEmailAndDocument(anyString(), anyString())).thenReturn(Mono.just(true));
        when(loanTypeGateway.existById(anyLong())).thenReturn(Mono.just(true));
        when(loanApplicationGateway.saveLoan(any(LoanApplication.class)))
                .thenReturn(Mono.just(loanApplication.toBuilder().idLoan(1L).build()));

        Mono<LoanApplication> result = useCase.create(loanApplication);

        StepVerifier.create(result)
                .expectNextMatches(saved -> 
                        saved.getIdLoan() != null && 
                        saved.getDocumentNumber().equals(loanApplication.getDocumentNumber()))
                .verifyComplete();

        verify(clientValidationGateway).validateUserByEmailAndDocument(
                loanApplication.getEmail(), 
                loanApplication.getDocumentNumber());
        verify(loanTypeGateway).existById(loanApplication.getLoanType());
        verify(loanApplicationGateway).saveLoan(loanApplication);
    }

    @Test
    void createLoanApplication_WithInvalidClient_ShouldReturnError() {
        when(clientValidationGateway.validateUserByEmailAndDocument(anyString(), anyString())).thenReturn(Mono.just(false));

        Mono<LoanApplication> result = useCase.create(loanApplication);

        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                        error instanceof BusinessRuleViolationException && 
                        ((BusinessRuleViolationException) error).code().equals(ResponseCode.CLIENT_VALIDATION_ERROR.getCodeValue()))
                .verify();
                
        verify(clientValidationGateway).validateUserByEmailAndDocument(
                loanApplication.getEmail(), 
                loanApplication.getDocumentNumber());
    }
    
    @Test
    void createLoanApplication_WithNonExistentLoanType_ShouldReturnBusinessError() {
        when(clientValidationGateway.validateUserByEmailAndDocument(anyString(), anyString())).thenReturn(Mono.just(true));
        when(loanTypeGateway.existById(anyLong())).thenReturn(Mono.just(false));

        Mono<LoanApplication> result = useCase.create(loanApplication);

        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                        error instanceof BusinessRuleViolationException && 
                        ((BusinessRuleViolationException) error).code().equals(ResponseCode.LOAN_TYPE_NOT_FOUND.getCodeValue()))
                .verify();
                
        verify(clientValidationGateway).validateUserByEmailAndDocument(
                loanApplication.getEmail(), 
                loanApplication.getDocumentNumber());
        verify(loanTypeGateway).existById(loanApplication.getLoanType());
    }
}
