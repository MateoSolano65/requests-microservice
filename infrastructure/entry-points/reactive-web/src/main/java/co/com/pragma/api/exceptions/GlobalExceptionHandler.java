package co.com.pragma.api.exceptions;

import co.com.pragma.api.dto.ErrorInfoDto;
import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ResourceConflictException;
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
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final LinkedHashMap<Class<?>, HttpStatus> httpStatusCodes = new LinkedHashMap<>();
    private final LinkedHashMap<Class<?>, BiFunction<Throwable, HttpStatus, ResponseApiDto<Object>>> bodyBuilders = new LinkedHashMap<>();
    private final LinkedHashSet<Class<?>> businessExceptions = new LinkedHashSet<>();

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());

        register(BusinessRuleViolationException.class, HttpStatus.BAD_REQUEST, this::fromBusinessLike, true);
        register(ResourceConflictException.class, HttpStatus.CONFLICT, this::fromBusinessLike, true);
        register(WebExchangeBindException.class, HttpStatus.UNPROCESSABLE_ENTITY, this::fromWebExchangeBind, true);
        register(ConstraintViolationException.class, HttpStatus.UNPROCESSABLE_ENTITY, this::fromConstraintViolation, true);
    }

    private void register(Class<? extends Throwable> type,
                        HttpStatus status,
                        BiFunction<Throwable, HttpStatus, ResponseApiDto<Object>> bodyBuilder,
                        boolean business) {
        httpStatusCodes.put(type, status);
        bodyBuilders.put(type, bodyBuilder);
        if (business) businessExceptions.add(type);
    }

    private Mono<ServerResponse> errorResponse(ServerRequest request) {
        Throwable throwable = this.getError(request);
        String message = throwable.getMessage();

        HttpStatus responseStatus = resolveStatus(throwable);
        ResponseApiDto<Object> response = createErrorResponse(throwable, responseStatus);

        boolean isBusinessException = isBusiness(throwable);

        if (!isBusinessException) {
            logger.error(message, throwable);
        }

        return ServerResponse.status(responseStatus)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(response);
    }

    private HttpStatus resolveStatus(Throwable throwable) {
        if (throwable instanceof ResponseStatusException responseStatusException) {
            return HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        }
        HttpStatus mapped = lookupAssignable(httpStatusCodes, throwable);
        if (mapped != null) return mapped;
        if (Exceptions.isMultiple(throwable)) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseApiDto<Object> createErrorResponse(Throwable throwable, HttpStatus status) {
        BiFunction<Throwable, HttpStatus, ResponseApiDto<Object>> builder = lookupAssignable(bodyBuilders, throwable);
        if (builder != null) {
            return builder.apply(throwable, status);
        }

        if (throwable instanceof ResponseStatusException rse) {
            return ResponseApiDto.<Object>builder()
                .status(status.value())
                .error(ErrorInfoDto.builder()
                    .code(status.name())
                    .detail(Optional.ofNullable(rse.getReason()).orElse(status.getReasonPhrase()))
                    .build())
                .build();
        }

        String detail = (status == HttpStatus.INTERNAL_SERVER_ERROR)
                ? "An unexpected error occurred"
                : Optional.ofNullable(throwable.getMessage()).orElse(status.getReasonPhrase());

        return ResponseApiDto.<Object>builder()
            .status(status.value())
            .error(ErrorInfoDto.builder()
                .code(status == HttpStatus.INTERNAL_SERVER_ERROR ? "INTERNAL_ERROR" : status.name())
                .detail(detail)
                .build())
            .build();
    }

    private ResponseApiDto<Object> fromBusinessLike(Throwable t, HttpStatus status) {
        String code;
        String detail = Optional.ofNullable(t.getMessage()).orElse(status.getReasonPhrase());

        if (t instanceof BusinessRuleViolationException ex) {
            code = ex.code();
        } else if (t instanceof ResourceConflictException ex) {
            code = ex.code();
        } else {
            code = "BUSINESS_ERROR";
        }

        return ResponseApiDto.<Object>builder()
            .status(status.value())
            .error(ErrorInfoDto.builder().code(code).detail(detail).build())
            .build();
    }

    private ResponseApiDto<Object> fromWebExchangeBind(Throwable t, HttpStatus status) {
        WebExchangeBindException ex = (WebExchangeBindException) t;

        Map<String, Object> meta = new HashMap<>();
        List<Map<String, String>> violations = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> {
                Map<String, String> violation = new HashMap<>();
                violation.put("field", fieldError.getField());
                violation.put("message", Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid"));
                return violation;
            })
            .collect(Collectors.toList());

        meta.put("violations", violations);

        return ResponseApiDto.<Object>builder()
            .status(status.value())
            .error(ErrorInfoDto.builder()
                .code("VALIDATION_ERROR")
                .detail("Invalid request body")
                .build())
            .meta(meta)
            .build();
    }

    private ResponseApiDto<Object> fromConstraintViolation(Throwable t, HttpStatus status) {
        ConstraintViolationException ex = (ConstraintViolationException) t;

        Map<String, Object> meta = new HashMap<>();
        List<Map<String, String>> violations = ex.getConstraintViolations()
            .stream()
            .map(violation -> {
                Map<String, String> violationMap = new HashMap<>();
                String field = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
                if (field.contains(".")) {
                    field = field.substring(field.lastIndexOf('.') + 1);
                }
                violationMap.put("field", field);
                violationMap.put("message", violation.getMessage());
                return violationMap;
            })
            .collect(Collectors.toList());

        meta.put("violations", violations);

        return ResponseApiDto.<Object>builder()
            .status(status.value())
            .error(ErrorInfoDto.builder()
                .code("VALIDATION_ERROR")
                .detail("Invalid request body")
                .build())
            .meta(meta)
            .build();
    }

    private <T> T lookupAssignable(LinkedHashMap<Class<?>, T> map, Throwable t) {
        for (Map.Entry<Class<?>, T> e : map.entrySet()) {
            if (e.getKey().isInstance(t)) return e.getValue();
        }
        return null;
    }

    private boolean isBusiness(Throwable t) {
        if (Exceptions.isMultiple(t)) return true;
        for (Class<?> c : businessExceptions) {
            if (c.isInstance(t)) return true;
        }
        return false;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::errorResponse);
    }
}
