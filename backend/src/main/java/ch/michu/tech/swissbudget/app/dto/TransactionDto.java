package ch.michu.tech.swissbudget.app.dto;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;

import ch.michu.tech.swissbudget.framework.validation.Nullable;
import ch.michu.tech.swissbudget.framework.validation.ValidateAmount;
import ch.michu.tech.swissbudget.framework.validation.ValidateLength;
import ch.michu.tech.swissbudget.framework.validation.ValidatedDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.time.LocalDate;
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

    private String id;
    private boolean expense;
    @ValidateAmount(min = 0)
    private double amount;
    private LocalDate transactionDate;
    private String bankAccount;
    private String receiver;

    @Nullable
    @ValidateAmount(min = 0)
    private int tagId;
    @Nullable
    @ValidateAmount(min = 0)
    private int matchingKeywordId;
    @Nullable
    @ValidateLength(min = 2, max = 20)
    private String alias;
    @Nullable
    @ValidateLength(min = 5, max = 250)
    private String note;

    public TransactionDto(String id, boolean expense, double amount, LocalDate transactionDate, String bankAccount, String receiver) {
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
}
