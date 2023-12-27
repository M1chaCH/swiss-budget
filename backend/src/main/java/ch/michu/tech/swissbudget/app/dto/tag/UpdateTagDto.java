package ch.michu.tech.swissbudget.app.dto.tag;

import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import java.util.List;
import java.util.UUID;
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
public class UpdateTagDto {

    @ValidateAmount(min = 1)
    @Nullable
    private UUID tagId;
    private String icon;
    private String color;
    @ValidateLength(min = 3, max = 50)
    private String name;
    private List<String> keywordsToAdd;
    @Nullable
    private UUID[] keywordIdsToDelete;
}