package co.com.pragma.api.exceptions;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ResourceConflictException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Exceptions;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    @Test
    void shouldReturn409ForResourceConflict() {
        TestableHandler handler = testable(new ResourceConflictException("User already exists"));
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturn400ForBusinessRuleViolation() {
        TestableHandler handler = testable(new BusinessRuleViolationException("Rule broken"));
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn422ForConstraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("user.email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        ConstraintViolationException cve = new ConstraintViolationException(Set.of(violation));

        TestableHandler handler = testable(cve);
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void shouldHonorResponseStatusException() {
        TestableHandler handler = testable(new ResponseStatusException(HttpStatus.NOT_FOUND, "missing"));
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn400ForReactorMultiple() {
        Throwable composite = Exceptions.multiple(List.of(new RuntimeException("a"), new RuntimeException("b")));
        TestableHandler handler = testable(composite);
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldReturn500ForUnexpected() {
        TestableHandler handler = testable(new RuntimeException("boom"));
        RouterFunction<?> rf = handler.getRoutingFunction(handler.errorAttributes);

        WebTestClient.bindToRouterFunction(rf).build()
                .get().uri("/any")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(ResponseApiDto.class)
                .consumeWith(r -> {});
    }

    private static TestableHandler testable(Throwable t) {
        ErrorAttributes ea = Mockito.mock(ErrorAttributes.class);
        ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
        when(ctx.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        ServerCodecConfigurer codecs = ServerCodecConfigurer.create();
        return new TestableHandler(ea, ctx, codecs, t);
    }

    static class TestableHandler extends GlobalExceptionHandler {
        final ErrorAttributes errorAttributes;
        Throwable throwable;
        TestableHandler(ErrorAttributes ea, ApplicationContext ctx, ServerCodecConfigurer cfg, Throwable th) {
            super(ea, ctx, cfg);
            this.errorAttributes = ea;
            this.throwable = th;
        }
        @Override
        protected Throwable getError(org.springframework.web.reactive.function.server.ServerRequest request) {
            return throwable;
        }
    }
}
