package ch.michu.tech.swissbudget.app.dto.transaction;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class TransactionDto {

    private UUID id;
    private boolean expense;
    @ValidateAmount(min = 0)
    private double amount;
    private LocalDate transactionDate;
    private String bankAccount;
    private String receiver;

    @Nullable
    @ValidateAmount(min = 0)
    private UUID tagId;
    @Nullable
    private TagDto tag;
    @Nullable
    @ValidateAmount(min = 0)
    private UUID matchingKeywordId;
    @Nullable
    private KeywordDto matchingKeyword;
    @Nullable
    @ValidateLength(max = 50)
    private String alias;
    @Nullable
    @ValidateLength(max = 250)
    private String note;
    @Nullable
    private boolean needsAttention;

    @Nullable
    private List<TransactionTagDuplicateDto> duplicatedTagMatches = new ArrayList<>();

    public TransactionDto(UUID id, boolean expense, double amount, LocalDate transactionDate, String bankAccount, String receiver) {
        this.id = id;
        this.expense = expense;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.bankAccount = bankAccount;
        this.receiver = receiver;
    }

    public TransactionDto(TransactionRecord entity) {
        id = entity.getId();
        expense = entity.getExpense();
        amount = entity.getAmount();
        transactionDate = entity.getTransactionDate();
        bankAccount = entity.getBankaccount();
        receiver = entity.getReceiver();
        needsAttention = entity.getNeedUserAttention();

        if (entity.get(TRANSACTION.TAG_ID) != null) {
            tagId = entity.getTagId();
        }
        if (entity.get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
            matchingKeywordId = entity.getMatchingKeywordId();
        }
        if (entity.get(TRANSACTION.ALIAS) != null) {
            alias = entity.getAlias();
        }
        if (entity.get(TRANSACTION.NOTE) != null) {
            note = entity.getNote();
        }
    }

    public TransactionDto(CompleteTransactionEntity entity) {
        final TransactionRecord transaction = entity.getTransaction();
        id = transaction.getId();
        expense = transaction.getExpense();
        amount = transaction.getAmount();
        transactionDate = transaction.getTransactionDate();
        bankAccount = transaction.getBankaccount();
        receiver = transaction.getReceiver();
        needsAttention = transaction.getNeedUserAttention();

        if (transaction.get(TRANSACTION.TAG_ID) != null) {
            tagId = transaction.getTagId();
        }
        if (transaction.get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
            matchingKeywordId = transaction.getMatchingKeywordId();
        }
        if (transaction.get(TRANSACTION.ALIAS) != null) {
            alias = transaction.getAlias();
        }
        if (transaction.get(TRANSACTION.NOTE) != null) {
            note = transaction.getNote();
        }

        if (entity.getTag() != null) {
            tag = new TagDto(entity.getTag());
        }
        if (entity.getMatchingKeyword() != null) {
            matchingKeyword = new KeywordDto(entity.getMatchingKeyword());
        }
    }
}
