package ch.michu.tech.swissbudget.app.dto;

import ch.michu.tech.swissbudget.framework.interceptor.validation.ValidateLength;
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
public class CreateMailFolderDto {

    @ValidateLength(min = 3, max = 32)
    private String folderName;
    private CredentialDto credentials;
}
