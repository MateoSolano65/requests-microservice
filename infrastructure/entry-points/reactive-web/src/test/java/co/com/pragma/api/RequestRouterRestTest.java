package co.com.pragma.api;

import co.com.pragma.api.handler.LoanTypeHandler;
import co.com.pragma.api.handler.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class RequestRouterRestTest {

    @Test
    @DisplayName("POST /api/v1/solicitud enruta a RequestHandler.createLoanApplication")
    void postSolicitud_RoutesToCreateLoanApplication() {
        RequestHandler requestHandler = Mockito.mock(RequestHandler.class);
        LoanTypeHandler loanTypeHandler = Mockito.mock(LoanTypeHandler.class);
        RouterFunction<ServerResponse> router = new RequestRouterRest().routerFunction(requestHandler, loanTypeHandler);
        WebTestClient client = WebTestClient.bindToRouterFunction(router).build();

        given(requestHandler.createLoanApplication(any())).willReturn(ServerResponse.ok().build());

        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk();

        then(requestHandler).should().createLoanApplication(any());
    }

    @Test
    @DisplayName("GET /api/v1/tipos-prestamo enruta a LoanTypeHandler.getAllLoanTypes")
    void getTiposPrestamo_RoutesToGetAllLoanTypes() {
        RequestHandler requestHandler = Mockito.mock(RequestHandler.class);
        LoanTypeHandler loanTypeHandler = Mockito.mock(LoanTypeHandler.class);
        RouterFunction<ServerResponse> router = new RequestRouterRest().routerFunction(requestHandler, loanTypeHandler);
        WebTestClient client = WebTestClient.bindToRouterFunction(router).build();

        given(loanTypeHandler.getAllLoanTypes(any()))
                .willReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue("[]"));

        client.get()
                .uri("/api/v1/tipos-prestamo")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);

        then(loanTypeHandler).should().getAllLoanTypes(any());
    }
}
