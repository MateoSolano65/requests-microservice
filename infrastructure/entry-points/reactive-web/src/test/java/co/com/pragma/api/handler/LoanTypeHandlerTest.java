package co.com.pragma.api.handler;

import co.com.pragma.api.dto.LoanTypeDTO;
import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.mapper.LoanTypeMapper;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.usecase.loantype.LoanTypeUseCase;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeHandlerTest {

    @Mock
    private LoanTypeUseCase loanTypeUseCase;

    @Mock
    private LoanTypeMapper loanTypeMapper;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private LoanTypeHandler loanTypeHandler;

    private List<LoanType> loanTypes;
    private List<LoanTypeDTO> loanTypeDTOs;

    @BeforeEach
    void setUp() {
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

        loanTypeDTOs = Arrays.asList(
            LoanTypeDTO.builder()
                .id(1L)
                .name("Préstamo Personal")
                .minAmount(new BigDecimal("1000000"))
                .maxAmount(new BigDecimal("50000000"))
                .interestRate(new BigDecimal("0.12"))
                .automaticValidation(true)
                .build(),
            LoanTypeDTO.builder()
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
        when(loanTypeUseCase.getAllLoanTypes()).thenReturn(Flux.fromIterable(loanTypes));
        when(loanTypeMapper.toLoanTypeDTO(loanTypes.get(0))).thenReturn(loanTypeDTOs.get(0));
        when(loanTypeMapper.toLoanTypeDTO(loanTypes.get(1))).thenReturn(loanTypeDTOs.get(1));

        Mono<ServerResponse> response = loanTypeHandler.getAllLoanTypes(serverRequest);
        StepVerifier.create(response)
                .consumeNextWith(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
                })
                .verifyComplete();

        verify(loanTypeUseCase).getAllLoanTypes();
        verify(loanTypeMapper).toLoanTypeDTO(loanTypes.get(0));
        verify(loanTypeMapper).toLoanTypeDTO(loanTypes.get(1));
    }

    @Test
    void getAllLoanTypes_WithResponseBody_ShouldIncludeCorrectData() {
        when(loanTypeUseCase.getAllLoanTypes()).thenReturn(Flux.fromIterable(loanTypes));
        when(loanTypeMapper.toLoanTypeDTO(loanTypes.get(0))).thenReturn(loanTypeDTOs.get(0));
        when(loanTypeMapper.toLoanTypeDTO(loanTypes.get(1))).thenReturn(loanTypeDTOs.get(1));

        Mono<ServerResponse> response = loanTypeHandler.getAllLoanTypes(serverRequest);
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllLoanTypes_WhenNoTypes_ShouldReturnEmptyList() {
        when(loanTypeUseCase.getAllLoanTypes()).thenReturn(Flux.empty());

        Mono<ServerResponse> response = loanTypeHandler.getAllLoanTypes(serverRequest);
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }
}
