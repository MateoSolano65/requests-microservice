package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {
    
    public LoanApplication toLoanApplication(LoanApplicationDTO loanApplicationDTO) {
        return LoanApplication.builder()
                .idLoan(loanApplicationDTO.getIdLoan())
                .loanAmount(loanApplicationDTO.getLoanAmount())
                .termInMonths(loanApplicationDTO.getTermInMonths())
                .documentNumber(loanApplicationDTO.getDocumentNumber())
                .loanType(loanApplicationDTO.getLoanType())
                .loanStatus(loanApplicationDTO.getLoanStatus())
                .build();
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
