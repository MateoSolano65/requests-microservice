package co.com.pragma.api.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class RegexTest {

    @ParameterizedTest
    @DisplayName("DOCUMENT_NUMBER_REGEX: válidos")
    @ValueSource(strings = {
            "12345",
            "00000",
            "9876543210",
            "12345678901234567890"
    })
    void documentNumber_valid(String value) {
        assertThat(Pattern.matches(Regex.DOCUMENT_NUMBER_REGEX, value)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("DOCUMENT_NUMBER_REGEX: inválidos")
    @ValueSource(strings = {
            "1234",
            "123456789012345678901",
            "12a45",
            " 12345",
            "12345 ",
            "12345abc",
            "",
            "-12345"
    })
    void documentNumber_invalid(String value) {
        assertThat(Pattern.matches(Regex.DOCUMENT_NUMBER_REGEX, value)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("EMAIL_REGEX: válidos")
    @ValueSource(strings = {
            "user@example.com",
            "USER.name+tag-123@sub.domain.co",
            "a@b.co",
            "x_y.z-9+q@a-b.cafe",
            "simple@localhost.localdomain"
    })
    void email_valid(String value) {
        assertThat(Pattern.matches(Regex.EMAIL_REGEX, value)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("EMAIL_REGEX: inválidos")
    @ValueSource(strings = {
            "no-at",
            "a@b",
            "a@b.c",
            "a@b.",
            "@b.com",
            "a@.com",
            "a b@c.com",
            "user@domain,com",
            "user@domain",
            "user@domain.c1"
    })
    void email_invalid(String value) {
        assertThat(Pattern.matches(Regex.EMAIL_REGEX, value)).isFalse();
    }
}
