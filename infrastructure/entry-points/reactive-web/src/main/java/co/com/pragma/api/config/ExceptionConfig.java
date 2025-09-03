package co.com.pragma.api.config;

import co.com.pragma.api.exceptions.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import java.util.Collections;
import java.util.List;

@Configuration
public class ExceptionConfig {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
    
    @Bean
    public List<ViewResolver> viewResolvers() {
        return Collections.emptyList();
    }

    @Bean
    @Order(-2)
    public GlobalExceptionHandler globalExceptionHandler(ErrorAttributes errorAttributes,
                                                       ApplicationContext applicationContext,
                                                       ServerCodecConfigurer serverCodecConfigurer) {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(
            errorAttributes, applicationContext, serverCodecConfigurer);
        exceptionHandler.setViewResolvers(viewResolvers());
        return exceptionHandler;
    }
}
