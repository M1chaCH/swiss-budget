package ch.michu.tech.swissbudget.app.dto.tag;

import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ValidatedDto
public class AssignTagDto {

    @ValidateLength(min = 1)
    private String transactionId;
    @ValidateAmount(min = 1)
    private String tagId;
    @Nullable
    @ValidateLength(min = 1)
    private String keyword;
}
