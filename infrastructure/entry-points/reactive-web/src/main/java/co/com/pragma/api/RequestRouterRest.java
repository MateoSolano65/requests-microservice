package co.com.pragma.api;

import co.com.pragma.api.handler.LoanTypeHandler;
import co.com.pragma.api.handler.RequestHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RequestRouterRest {
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = RequestHandler.class,
                    beanMethod = "createLoanApplication"
            ),
            @RouterOperation(
                    path = "/api/v1/tipos-prestamo",
                    method = RequestMethod.GET,
                    beanClass = LoanTypeHandler.class,
                    beanMethod = "getAllLoanTypes"
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(RequestHandler requestHandler, LoanTypeHandler loanTypeHandler) {
        return route(POST("/api/v1/solicitud"), requestHandler::createLoanApplication)
                .andRoute(GET("/api/v1/tipos-prestamo"), loanTypeHandler::getAllLoanTypes);
    }
}
