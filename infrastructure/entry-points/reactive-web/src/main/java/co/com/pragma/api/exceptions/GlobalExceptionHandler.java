package co.com.pragma.api.exceptions;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.response.ResponseCode;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.List;

public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ApplicationContext ctx,
                                  ServerCodecConfigurer codecs) {
        super(errorAttributes, new WebProperties.Resources(), ctx);
        setMessageWriters(codecs.getWriters());
        setMessageReaders(codecs.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleError);
    }

    private Mono<ServerResponse> handleError(ServerRequest request) {
        Throwable ex = getError(request);

        // 1) Negocio
        if (ex instanceof BusinessException be) {
            return buildResponse(HttpStatus.valueOf(be.statusCode()),
                    ResponseApiDto.builder()
                            .code(be.code())
                            .message(be.getMessage())
                            .build());
        }

        // 2) Validación
        if (ex instanceof WebExchangeBindException we) {
            List<String> errors = we.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + Optional.ofNullable(fe.getDefaultMessage()).orElse("Invalid"))
                    .toList();
            return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY,
                    ResponseApiDto.builder()
                            .code(ResponseCode.VALIDATION_ERROR.getCodeValue())
                            .message(ResponseCode.VALIDATION_ERROR.getDefaultMessage())
                            .error(errors)
                            .build());
        }
        if (ex instanceof ConstraintViolationException cve) {
            List<String> errors = cve.getConstraintViolations().stream()
                    .map(v -> {
                        String field = Optional.ofNullable(v.getPropertyPath()).map(Object::toString).orElse("");
                        int i = field.lastIndexOf('.');
                        if (i >= 0) field = field.substring(i + 1);
                        return field + ": " + v.getMessage();
                    })
                    .toList();
            return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY,
                    ResponseApiDto.builder()
                            .code(ResponseCode.VALIDATION_ERROR.getCodeValue())
                            .message(ResponseCode.VALIDATION_ERROR.getDefaultMessage())
                            .error(errors)
                            .build());
        }

        HttpStatus status = (ex instanceof ResponseStatusException rse)
                ? HttpStatus.valueOf(rse.getStatusCode().value())
                : HttpStatus.INTERNAL_SERVER_ERROR;

        ResponseCode rc = ResponseCode.findByHttpStatus(status.value());
        String detail = Optional.ofNullable(ex.getMessage()).orElse(rc.getDefaultMessage());

        if (!(ex instanceof ResponseStatusException)) {
            log.error(ex.getMessage(), ex);
        }

        return buildResponse(status,
                ResponseApiDto.builder()
                        .code(rc.getCodeValue())
                        .message(rc.getDefaultMessage())
                        .error(List.of(detail))
                        .build());
    }

    private Mono<ServerResponse> buildResponse(HttpStatus status, ResponseApiDto<?> body) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}

