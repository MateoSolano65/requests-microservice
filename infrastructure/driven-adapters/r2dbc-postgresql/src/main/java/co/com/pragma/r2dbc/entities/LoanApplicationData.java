package co.com.pragma.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("application")
public class LoanApplicationData {
    @Id
    @Column("id_application")
    private Long idLoan;
    
    @Column("amount")
    private BigDecimal loanAmount;
    
    @Column("term")
    private Long termInMonths;
    
    @Column("document_number")
    private String documentNumber;
    
    @Column("email")
    private String email;
    
    @Column("id_loan_type")
    private Long loanType;
    
    @Column("id_state")
    private Long loanStatus;
}
