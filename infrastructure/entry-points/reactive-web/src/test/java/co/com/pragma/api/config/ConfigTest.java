package co.com.pragma.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class ConfigTest {

    private WebTestClient client(String allowedOrigins, boolean includeExceptionHandler) {
        CorsWebFilter cors = new CorsConfig().corsWebFilter(allowedOrigins);
        SecurityHeadersConfig security = new SecurityHeadersConfig();

        RouterFunction<ServerResponse> router = route()
                .GET("/probe", req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("ok"))
                .GET("/boom", req -> Mono.error(new RuntimeException("boom")))
                .build();

        var builder = WebHttpHandlerBuilder
                .webHandler(RouterFunctions.toWebHandler(router))
                .filter(cors)
                .filter(security);

        if (includeExceptionHandler) {
            var exceptionHandler =
                    new ExceptionConfig().globalExceptionHandler(
                            new DefaultErrorAttributes(),
                            new AnnotationConfigApplicationContext(),
                            ServerCodecConfigurer.create()
                    );
            builder.exceptionHandler((WebExceptionHandler) exceptionHandler);
        }

        return WebTestClient.bindToWebHandler((WebHandler) builder.build()).configureClient().build();
    }

    @Test
    @DisplayName("Security headers aplicados en 200")
    void securityHeadersAplicados() {
        client("http://localhost:3000", false).get()
                .uri("/probe")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Test
    @DisplayName("GlobalExceptionHandler aplica headers en 5xx")
    void exceptionHandlerAplicaHeadersEnErrores() {
        client("http://localhost:3000", true).get()
                .uri("/boom")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}
