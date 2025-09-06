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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorDTOTest {

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<Object> violation1;

    @Mock
    private ConstraintViolation<Object> violation2;

    private ValidatorDTO validatorDTO;

    @BeforeEach
    void setUp() {
        validatorDTO = new ValidatorDTO(validator);
    }

    @Test
    void validate_DefersUntilSubscribe() {
        Object obj = new Object();
        Mono<Object> mono = validatorDTO.validate(obj);
        verifyNoInteractions(validator);
        when(validator.validate(obj)).thenReturn(Collections.emptySet());
        StepVerifier.create(mono)
                .expectNext(obj)
                .verifyComplete();
        verify(validator).validate(obj);
    }

    @Test
    void validate_WithValidObject_ReturnsSameObject() {
        Object obj = new Object();
        when(validator.validate(obj)).thenReturn(Collections.emptySet());

        StepVerifier.create(validatorDTO.validate(obj))
                .expectNext(obj)
                .verifyComplete();

        verify(validator).validate(obj);
    }

    @Test
    void validate_WithSingleViolation_EmitsConstraintViolationException() {
        Object obj = new Object();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation1);
        when(validator.validate(obj)).thenReturn(violations);

        StepVerifier.create(validatorDTO.validate(obj))
                .expectErrorSatisfies(err -> {
                    assert err instanceof ConstraintViolationException;
                    ConstraintViolationException cve = (ConstraintViolationException) err;
                    assert cve.getConstraintViolations().size() == 1;
                    assert cve.getConstraintViolations().contains(violation1);
                })
                .verify();

        verify(validator).validate(obj);
    }

    @Test
    void validate_WithMultipleViolations_PropagatesAllViolations() {
        Object obj = new Object();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);
        when(validator.validate(obj)).thenReturn(violations);

        StepVerifier.create(validatorDTO.validate(obj))
                .expectErrorSatisfies(err -> {
                    assert err instanceof ConstraintViolationException;
                    ConstraintViolationException cve = (ConstraintViolationException) err;
                    assert cve.getConstraintViolations().size() == 2;
                    assert cve.getConstraintViolations().containsAll(violations);
                })
                .verify();

        verify(validator).validate(obj);
    }

    @Test
    void validate_WhenValidatorThrows_PropagatesError() {
        Object obj = new Object();
        RuntimeException expected = new RuntimeException("Validation error");
        when(validator.validate(any())).thenThrow(expected);

        StepVerifier.create(validatorDTO.validate(obj))
                .expectErrorMatches(e -> e == expected)
                .verify();
    }
}
