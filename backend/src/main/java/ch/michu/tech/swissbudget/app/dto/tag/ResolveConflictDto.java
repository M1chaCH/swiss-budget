package ch.michu.tech.swissbudget.app.dto.tag;

import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
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
public class ResolveConflictDto {

    private UUID transactionId;
    private UUID selectedTagId;
    private UUID matchingKeywordId;
    private boolean removeUnselectedKeywords;
}
