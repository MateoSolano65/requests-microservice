package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanTypeDTO;
import co.com.pragma.model.loantype.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanTypeMapperTest {

    private LoanTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoanTypeMapper();
    }

    @Test
    void toLoanTypeDTO_mapsAllFieldsCorrectly() {
        LoanType domain = LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .minAmount(new BigDecimal("1000.00"))
                .maxAmount(new BigDecimal("5000.00"))
                .interestRate(new BigDecimal("12.5"))
                .automaticValidation(true)
                .build();

        LoanTypeDTO dto = mapper.toLoanTypeDTO(domain);

        assertThat(dto.getId()).isEqualTo(domain.getId());
        assertThat(dto.getName()).isEqualTo(domain.getName());
        assertThat(dto.getMinAmount()).isEqualTo(domain.getMinAmount());
        assertThat(dto.getMaxAmount()).isEqualTo(domain.getMaxAmount());
        assertThat(dto.getInterestRate()).isEqualTo(domain.getInterestRate());
        assertThat(dto.getAutomaticValidation()).isEqualTo(domain.getAutomaticValidation());
    }

    @Test
    void toLoanTypeDTO_withNullValues_shouldMapNulls() {
        LoanType domain = LoanType.builder().build();

        LoanTypeDTO dto = mapper.toLoanTypeDTO(domain);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getMinAmount()).isNull();
        assertThat(dto.getMaxAmount()).isNull();
        assertThat(dto.getInterestRate()).isNull();
        assertThat(dto.getAutomaticValidation()).isNull();
    }
}
