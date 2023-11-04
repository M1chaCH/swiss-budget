package ch.michu.tech.swissbudget.app.entity;

import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionTagDuplicateEntity {

    private TransactionRecord transaction;
    private TagRecord tag;
    private KeywordRecord matchingKeyword;
}
