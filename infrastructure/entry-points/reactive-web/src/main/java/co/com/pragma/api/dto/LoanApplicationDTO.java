package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationDTO {
    private Long idLoan;
    private Long loanAmount;
    private Long termInMonths;
    private String documentNumber;
    private Long loanType;
    private Long loanStatus;
}
