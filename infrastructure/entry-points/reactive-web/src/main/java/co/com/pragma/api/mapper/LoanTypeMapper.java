package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanTypeDTO;
import co.com.pragma.model.loantype.LoanType;
import org.springframework.stereotype.Component;

@Component
public class LoanTypeMapper {
    
    public LoanTypeDTO toLoanTypeDTO(LoanType loanType) {
        return LoanTypeDTO.builder()
                .id(loanType.getId())
                .name(loanType.getName())
                .minAmount(loanType.getMinAmount())
                .maxAmount(loanType.getMaxAmount())
                .interestRate(loanType.getInterestRate())
                .automaticValidation(loanType.getAutomaticValidation())
                .build();
    }
}
