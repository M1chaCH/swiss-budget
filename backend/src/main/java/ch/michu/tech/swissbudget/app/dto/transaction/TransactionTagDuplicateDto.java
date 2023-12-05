package ch.michu.tech.swissbudget.app.dto.transaction;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
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
public class TransactionTagDuplicateDto {

    private String transactionId;
    private TagDto tag;
    private KeywordDto matchingKeyword;
}
