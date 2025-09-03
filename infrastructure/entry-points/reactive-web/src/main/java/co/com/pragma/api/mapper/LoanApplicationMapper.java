package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.loanstatus.LoanStatusUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationMapper {
    
    private final LoanStatusUseCase loanStatusUseCase;

    public Mono<LoanApplication> toLoanApplicationWithPendingStatus(LoanApplicationDTO loanApplicationDTO) {
        return loanStatusUseCase.getPendingReviewStatusId()
                .map(pendingStatusId -> LoanApplication.builder()
                        .idLoan(loanApplicationDTO.getIdLoan())
                        .loanAmount(loanApplicationDTO.getLoanAmount())
                        .termInMonths(loanApplicationDTO.getTermInMonths())
                        .documentNumber(loanApplicationDTO.getDocumentNumber())
                        .loanType(loanApplicationDTO.getLoanType())
                        .loanStatus(pendingStatusId) 
                        .build());
    }
    
    public LoanApplicationDTO toLoanApplicationDTO(LoanApplication loanApplication) {
        return LoanApplicationDTO.builder()
                .idLoan(loanApplication.getIdLoan())
                .loanAmount(loanApplication.getLoanAmount())
                .termInMonths(loanApplication.getTermInMonths())
                .documentNumber(loanApplication.getDocumentNumber())
                .loanType(loanApplication.getLoanType())
                .loanStatus(loanApplication.getLoanStatus())
                .build();
    }
}
