package co.com.pragma.api;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.usecase.createapplication.CreateLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
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

    public Mono<ServerResponse> createLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDTO.class)
                .flatMap(loanApplicationDTO -> {
                    var loanApplication = loanApplicationMapper.toLoanApplication(loanApplicationDTO);
                    return createLoanApplicationUseCase.createLoanApplication(loanApplication)
                            .flatMap(savedLoan -> {
                                var responseDTO = loanApplicationMapper.toLoanApplicationDTO(savedLoan);
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(responseDTO);
                            });
                })
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }
}
