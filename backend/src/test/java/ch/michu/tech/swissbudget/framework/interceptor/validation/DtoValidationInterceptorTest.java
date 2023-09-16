package ch.michu.tech.swissbudget.framework.interceptor.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.michu.tech.swissbudget.framework.error.exception.DtoValidationException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

class DtoValidationInterceptorTest {

    @Test
    void validateArg_Mail() {
        DtoValidationInterceptor interceptor = new DtoValidationInterceptor();
        ValidationTestDto dto = new ValidationTestDto();
        dto.setAmount(13);

        dto.setMail("invalid.mail");
        assertThrows(DtoValidationException.class,
            () -> interceptor.validateArg(dto));

        dto.setMail("v.33@m.com");
        assertDoesNotThrow(
            () -> interceptor.validateArg(dto));

        dto.setMail("way.to.long.mail@test.com");
        assertThrows(DtoValidationException.class, () -> interceptor.validateArg(dto));
    }

    @Test
    void validateArg_Length() {
        DtoValidationInterceptor interceptor = new DtoValidationInterceptor();
        ValidationTestDto dto = new ValidationTestDto();
        dto.setAmount(13);

        dto.setElevenString("12345678910");
        assertDoesNotThrow(() -> interceptor.validateArg(dto));

        dto.setElevenString("1");
        assertThrows(DtoValidationException.class, () -> interceptor.validateArg(dto));
    }

    @Test
    void validateArg_Amount() {
        DtoValidationInterceptor interceptor = new DtoValidationInterceptor();
        ValidationTestDto dto = new ValidationTestDto();

        dto.setAmount(14);
        assertDoesNotThrow(() -> interceptor.validateArg(dto));

        dto.setAmount(12);
        assertThrows(DtoValidationException.class, () -> interceptor.validateArg(dto));
        dto.setAmount(16);
        assertThrows(DtoValidationException.class, () -> interceptor.validateArg(dto));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    @ValidatedDto
    private class ValidationTestDto {

        @ValidateMail
        @ValidateLength(max = 10)
        private String mail;

        @ValidateLength(min = 11)
        private String elevenString;

        @ValidateAmount(min = 12.2, max = 15.83)
        private int amount;
    }
}