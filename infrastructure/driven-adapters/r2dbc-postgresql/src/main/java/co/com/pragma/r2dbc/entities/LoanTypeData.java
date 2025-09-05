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
@Table("loan_type")
public class LoanTypeData {
    @Id
    @Column("id_loan_type")
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("min_amount")
    private BigDecimal minAmount;
    
    @Column("max_amount")
    private BigDecimal maxAmount;
    
    @Column("interest_rate")
    private BigDecimal interestRate;
    
    @Column("automatic_validation")
    private Boolean automaticValidation;
}
