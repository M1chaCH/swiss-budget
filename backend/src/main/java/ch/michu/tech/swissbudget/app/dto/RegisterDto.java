package ch.michu.tech.swissbudget.app.dto;

import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidateMail;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
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
public class RegisterDto {

    @ValidateLength(min = 3, max = 32)
    private String folderName;
    private String bank;

    @ValidateMail
    private String mail;
    private String password;
}
