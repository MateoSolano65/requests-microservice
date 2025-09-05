package co.com.pragma.api.config;

import co.com.pragma.api.handler.LoanTypeHandler;
import co.com.pragma.api.handler.RequestHandler;
import co.com.pragma.api.RequestRouterRest;
import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.api.mapper.LoanTypeMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import co.com.pragma.usecase.loantype.LoanTypeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {RequestRouterRest.class, RequestHandler.class, LoanTypeHandler.class, ConfigTest.TestConfig.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Configuration
    static class TestConfig {
        @Bean
        public LoanApplicationUseCase loanApplicationUseCase() {
            return mock(LoanApplicationUseCase.class);
        }
        
        @Bean
        public LoanTypeUseCase loanTypeUseCase() {
            return mock(LoanTypeUseCase.class);
        }
        
        @Bean
        public LoanApplicationMapper loanApplicationMapper() {
            return mock(LoanApplicationMapper.class);
        }
        
        @Bean
        public LoanTypeMapper loanTypeMapper() {
            return mock(LoanTypeMapper.class);
        }
        
        @Bean
        public ValidatorDTO validatorDTO() {
            return mock(ValidatorDTO.class);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}