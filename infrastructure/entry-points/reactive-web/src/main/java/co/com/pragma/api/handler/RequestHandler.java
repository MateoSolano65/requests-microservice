package co.com.pragma.api.handler;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.usecase.createapplication.CreateLoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestHandler {
    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;
    private final ValidatorDTO validatorDTO;

    @Operation(
            operationId = "createLoanApplication",
            summary = "Crear un nuevo préstamo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.
                    RequestBody(required = true,
                    content = @Content(schema = @Schema(implementation = LoanApplicationDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
                    @ApiResponse(responseCode = "422", description = "Error de validación",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class)))
            }
    )
    public Mono<ServerResponse> createLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDTO.class)
                .flatMap(validatorDTO::validate)
                .flatMap(loanApplicationMapper::toLoanApplicationWithPendingStatus)
                .flatMap(loanApplication -> {
                    final String dummyToken = "dummy-token"; 
                    return createLoanApplicationUseCase.create(dummyToken, loanApplication);
                })
                .map(loanApplicationMapper::toLoanApplicationDTO)
                .flatMap(loanDTO -> {
                    ResponseCode successCode = ResponseCode.LOAN_APPLICATION_CREATED;
                    ResponseApiDto<LoanApplicationDTO> response = ResponseApiDto.<LoanApplicationDTO>builder()
                            .code(successCode.getCodeValue())
                            .message(successCode.getDefaultMessage())
                            .data(loanDTO)
                            .build();
                    
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }
}
