package co.com.pragma.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    Long idLoan;
    BigDecimal loanAmount;
    Long termInMonths;
    String documentNumber;
    String email;
    Long loanType;
    Long loanStatus;
}
