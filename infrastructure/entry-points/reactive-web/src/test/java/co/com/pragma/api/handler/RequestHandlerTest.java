package co.com.pragma.api.handler;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHandlerTest {

    @Mock
    private LoanApplicationUseCase loanApplicationUseCase;

    @Mock
    private LoanApplicationMapper loanApplicationMapper;

    @Mock
    private ValidatorDTO validatorDTO;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private RequestHandler requestHandler;

    private LoanApplicationDTO loanApplicationDTO;
    private LoanApplication loanApplication;
    private LoanApplication savedLoanApplication;

    @BeforeEach
    void setUp() {
        loanApplicationDTO = LoanApplicationDTO.builder()
                .loanAmount(new BigDecimal("5000000.00"))
                .termInMonths(24L)
                .documentNumber("1098765432")
                .email("test@example.com")
                .loanType(1L)
                .build();

        loanApplication = LoanApplication.builder()
                .loanAmount(new BigDecimal("5000000.00"))
                .termInMonths(24L)
                .documentNumber("1098765432")
                .email("test@example.com")
                .loanType(1L)
                .loanStatus(1L)
                .build();

        savedLoanApplication = loanApplication.toBuilder()
                .idLoan(1L)
                .build();
    }

    @Test
    void createLoanApplication_WithValidRequest_ShouldReturnCreatedResponse() {
        when(serverRequest.bodyToMono(LoanApplicationDTO.class)).thenReturn(Mono.just(loanApplicationDTO));
        when(validatorDTO.validate(any(LoanApplicationDTO.class))).thenReturn(Mono.just(loanApplicationDTO));
        when(loanApplicationMapper.toLoanApplicationWithPendingStatus(any(LoanApplicationDTO.class))).thenReturn(Mono.just(loanApplication));
        when(loanApplicationUseCase.create(any(LoanApplication.class))).thenReturn(Mono.just(savedLoanApplication));
        when(loanApplicationMapper.toLoanApplicationDTO(any(LoanApplication.class))).thenReturn(loanApplicationDTO);

        Mono<ServerResponse> response = requestHandler.createLoanApplication(serverRequest);
        StepVerifier.create(response)
                .consumeNextWith(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.CREATED);
                })
                .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationDTO.class);
        verify(validatorDTO).validate(loanApplicationDTO);
        verify(loanApplicationMapper).toLoanApplicationWithPendingStatus(loanApplicationDTO);
        verify(loanApplicationUseCase).create(loanApplication);
        verify(loanApplicationMapper).toLoanApplicationDTO(savedLoanApplication);
    }

    @Test
    void createLoanApplication_WithResponseBody_ShouldIncludeCorrectData() {
        LoanApplicationDTO savedDTO = loanApplicationDTO.toBuilder().build();
        
        when(serverRequest.bodyToMono(LoanApplicationDTO.class)).thenReturn(Mono.just(loanApplicationDTO));
        when(validatorDTO.validate(any(LoanApplicationDTO.class))).thenReturn(Mono.just(loanApplicationDTO));
        when(loanApplicationMapper.toLoanApplicationWithPendingStatus(any(LoanApplicationDTO.class))).thenReturn(Mono.just(loanApplication));
        when(loanApplicationUseCase.create(any(LoanApplication.class))).thenReturn(Mono.just(savedLoanApplication));
        when(loanApplicationMapper.toLoanApplicationDTO(any(LoanApplication.class))).thenReturn(savedDTO);

        Mono<ServerResponse> response = requestHandler.createLoanApplication(serverRequest);
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void createLoanApplication_WhenValidationFails_ShouldPropagateError() {
        Exception validationException = new RuntimeException("Validation failed");
        
        when(serverRequest.bodyToMono(LoanApplicationDTO.class)).thenReturn(Mono.just(loanApplicationDTO));
        when(validatorDTO.validate(any(LoanApplicationDTO.class))).thenReturn(Mono.error(validationException));

        Mono<ServerResponse> response = requestHandler.createLoanApplication(serverRequest);
        StepVerifier.create(response)
                .expectErrorMatches(error -> error == validationException)
                .verify();

        verify(serverRequest).bodyToMono(LoanApplicationDTO.class);
        verify(validatorDTO).validate(loanApplicationDTO);
    }
}
