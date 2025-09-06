package co.com.pragma.config;

import co.com.pragma.model.loanapplication.gateways.ClientValidationGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationGateway;
import co.com.pragma.model.loanstatus.gateways.LoanStatusGateway;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeGateway;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public LoanApplicationGateway loanApplicationGateway() {
            return loanApplication -> Mono.empty();
        }

        @Bean
        public ClientValidationGateway clientValidationGateway() {
            return (email, document) -> Mono.empty();
        }

        @Bean
        public LoanStatusGateway loanStatusGateway() {
            return statusName -> Mono.empty();
        }

        @Bean
        public LoanTypeGateway loanTypeGateway() {
            return new LoanTypeGateway() {
                @Override
                public Flux<LoanType> getAllLoanTypes() {
                    return Flux.empty();
                }

                @Override
                public Mono<Boolean> existById(Long id) {
                    return Mono.empty();
                }
            };
        }

    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}