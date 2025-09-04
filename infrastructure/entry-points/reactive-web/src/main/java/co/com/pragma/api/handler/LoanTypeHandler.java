package co.com.pragma.api.handler;

import co.com.pragma.api.dto.LoanTypeDTO;
import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.mapper.LoanTypeMapper;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.usecase.loantype.LoanTypeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanTypeHandler {
    
    private final LoanTypeUseCase loanTypeUseCase;
    private final LoanTypeMapper loanTypeMapper;
    
    @Operation(
            operationId = "getLoanTypes",
            summary = "Obtener todos los tipos de préstamo disponibles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tipos de préstamo obtenida exitosamente",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ResponseApiDto.class)))
            }
    )
    public Mono<ServerResponse> getAllLoanTypes(ServerRequest serverRequest) {
        return loanTypeUseCase.getAllLoanTypes()
                .map(loanTypeMapper::toLoanTypeDTO)
                .collectList()
                .flatMap(loanTypes -> {
                    ResponseCode successCode = ResponseCode.LOAN_TYPES_FOUND;
                    ResponseApiDto<List<LoanTypeDTO>> response = ResponseApiDto.<List<LoanTypeDTO>>builder()
                            .code(successCode.getCodeValue())
                            .message(successCode.getDefaultMessage())
                            .data(loanTypes)
                            .build();
                    
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }
}
