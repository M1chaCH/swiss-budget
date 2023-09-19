package ch.michu.tech.swissbudget.app.dto;

import ch.michu.tech.swissbudget.framework.interceptor.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.interceptor.validation.ValidateMail;
import ch.michu.tech.swissbudget.framework.interceptor.validation.ValidatedDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@ValidatedDto
public class ContactMessageDto {

    @ValidateMail
    private String sourceAddress;
    @ValidateLength(min = 25, max = 500)
    private String message;
    @ValidateLength(min = 4)
    private String subject;
}
