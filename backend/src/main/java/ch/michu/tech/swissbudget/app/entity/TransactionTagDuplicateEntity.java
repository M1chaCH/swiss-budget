package ch.michu.tech.swissbudget.app.entity;

import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionTagDuplicateEntity {

    private UUID transactionId;
    private TagRecord tag;
    private KeywordRecord matchingKeyword;
}
