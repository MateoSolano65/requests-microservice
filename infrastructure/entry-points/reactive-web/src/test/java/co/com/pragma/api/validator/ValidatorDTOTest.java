package co.com.pragma.api.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorDTOTest {

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<Object> violation;

    private ValidatorDTO validatorDTO;

    @BeforeEach
    void setUp() {
        validatorDTO = new ValidatorDTO(validator);
    }

    @Test
    void validate_WithValidObject_ShouldReturnSameObject() {
        Object testObject = new Object();
        when(validator.validate(testObject)).thenReturn(Collections.emptySet());

        Mono<Object> result = validatorDTO.validate(testObject);
        StepVerifier.create(result)
                .expectNext(testObject)
                .verifyComplete();

        verify(validator).validate(testObject);
    }

    @Test
    void validate_WithInvalidObject_ShouldReturnValidationError() {
        Object testObject = new Object();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        when(validator.validate(testObject)).thenReturn(violations);

        Mono<Object> result = validatorDTO.validate(testObject);
        StepVerifier.create(result)
                .expectErrorMatches(error -> 
                        error instanceof ConstraintViolationException && 
                        ((ConstraintViolationException) error).getConstraintViolations().equals(violations))
                .verify();

        verify(validator).validate(testObject);
    }

    @Test
    void validate_WithMultipleViolations_ShouldReturnAllViolations() {
        Object testObject = new Object();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        violations.add(violation);  // Adding the same mock twice for simplicity
        when(validator.validate(testObject)).thenReturn(violations);

        Mono<Object> result = validatorDTO.validate(testObject);
        StepVerifier.create(result)
                .expectErrorMatches(error -> {
                    if (!(error instanceof ConstraintViolationException)) {
                        return false;
                    }
                    ConstraintViolationException cve = (ConstraintViolationException) error;
                    return cve.getConstraintViolations().size() == 2;
                })
                .verify();

        verify(validator).validate(testObject);
    }

    @Test
    void validate_WhenValidatorThrowsException_ShouldPropagateError() {
        Object testObject = new Object();
        RuntimeException expectedException = new RuntimeException("Validation error");
        when(validator.validate(any())).thenThrow(expectedException);

        Mono<Object> result = validatorDTO.validate(testObject);
        StepVerifier.create(result)
                .expectErrorMatches(error -> error == expectedException)
                .verify();
    }
}
