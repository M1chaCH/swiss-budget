package ch.michu.tech.swissbudget.app.dto;

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
public class CredentialDto {

    @ValidateMail
    private String mail;
    private String password;
}
