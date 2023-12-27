package ch.michu.tech.swissbudget.app.dto.tag;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.app.provider.TagProvider;
import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@ValidatedDto
public class TagDto {

    @ValidateAmount(min = 0)
    private UUID id;
    private String icon;
    private String color;
    @ValidateLength(min = 3, max = 50)
    private String name;
    @Nullable
    private boolean defaultTag;
    @Nullable
    private List<KeywordDto> keywords;

    public TagDto(TagRecord entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.defaultTag = entity.getDefaultTag();

        if (entity.get(TAG.ICON) != null) {
            this.icon = entity.getIcon();
        } else {
            this.icon = TagProvider.DEFAULT_TAG_ICON;
        }

        if (entity.get(TAG.COLOR) != null) {
            this.color = entity.getColor();
        } else {
            this.color = TagProvider.DEFAULT_TAG_COLOR;
        }
    }
}
