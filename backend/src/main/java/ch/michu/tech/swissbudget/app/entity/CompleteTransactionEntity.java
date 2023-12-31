package ch.michu.tech.swissbudget.app.entity;

import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTransactionEntity {

    private TransactionMailRecord mail;
    private TransactionRecord transaction;
    private TagRecord tag;
    private KeywordRecord matchingKeyword;
    private List<TransactionTagDuplicateEntity> tagDuplicates = new ArrayList<>();

    public void addTagDuplicate(TransactionTagDuplicateEntity other) {
        this.tagDuplicates.add(other);
        this.transaction.setNeedUserAttention(true);
    }
}
