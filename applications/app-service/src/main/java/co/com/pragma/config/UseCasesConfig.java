package co.com.pragma.config;

import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loanstatus.gateways.LoanStatusGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import co.com.pragma.usecase.loanstatus.LoanStatusUseCase;
import co.com.pragma.usecase.loantype.LoanTypeUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public LoanApplicationUseCase createLoanApplicationUseCase(
            LoanApplicationGateway loanApplicationGateway,
            ClientValidationGateway clientValidationGateway,
            LoanTypeGateway loanTypeGateway) {
        return new LoanApplicationUseCase(
                loanApplicationGateway,
                clientValidationGateway,
                loanTypeGateway);
    }

    @Bean
    public LoanStatusUseCase loanStatusUseCase(LoanStatusGateway loanStatusGateway) {
        return new LoanStatusUseCase(loanStatusGateway);
    }

    @Bean
    public LoanTypeUseCase loanTypeUseCase(LoanTypeGateway loanTypeGateway) {
        return new LoanTypeUseCase(loanTypeGateway);
    }
}
