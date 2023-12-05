package ch.michu.tech.swissbudget.app.dto.keyword;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;

import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
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
public class KeywordDto {

    @ValidateAmount(min = 0)
    private Integer id;
    private String keyword;
    @Nullable
    private Integer tagId;

    public KeywordDto(KeywordRecord entity) {
        this.id = entity.getId();
        this.keyword = entity.getKeyword();
        if (entity.get(KEYWORD.TAG_ID) != null) {
            this.tagId = entity.getTagId();
        }
    }
}