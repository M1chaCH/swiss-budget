package ch.michu.tech.swissbudget.app.transaction;

import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.entity.TransactionTagDuplicateEntity;
import ch.michu.tech.swissbudget.app.provider.TagProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider.TransactionIdWithTagDuplicateCount;
import ch.michu.tech.swissbudget.app.provider.TransactionTagDuplicateProvider;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.DSLContext;

@ApplicationScoped
public class TransactionTagMapper {

    private static final Logger LOGGER = Logger.getLogger(TransactionTagMapper.class.getSimpleName());

    private final TagProvider tagProvider;
    private final TransactionProvider transactionProvider;
    private final TransactionTagDuplicateProvider transactionTagDuplicateProvider;

    @Inject
    public TransactionTagMapper(TagProvider tagProvider, TransactionProvider transactionProvider,
        TransactionTagDuplicateProvider transactionTagDuplicateProvider) {
        this.tagProvider = tagProvider;
        this.transactionProvider = transactionProvider;
        this.transactionTagDuplicateProvider = transactionTagDuplicateProvider;
    }

    /**
     * sets the tag id and the matching keyword id in the given record if no tag matches, then the default tag is assigned
     *
     * @param userId       the userId to select the tags from
     * @param transactions the transactions to modify
     * @param store        whether the changes should be stored in the DB or not (would store using a transaction)
     */
    public void mapAll(DSLContext db, UUID userId, List<CompleteTransactionEntity> transactions, boolean store) {
        LOGGER.log(Level.INFO, "mapping {0} transactions for user {1}", new Object[]{transactions.size(), userId});
        Map<TagRecord, List<KeywordRecord>> tags = tagProvider.selectTagsWithKeywordsByUserId(db, userId);

        TagRecord defaultTag = getDefaultTag(tags.keySet());
        transactions.forEach(transaction -> map(transaction, tags, defaultTag));

        LOGGER.log(Level.INFO, "successfully mapped tags to transactions for {0}", new Object[]{userId});
        if (store) {
            transactionProvider.insertTransactions(db, transactions.stream().map(CompleteTransactionEntity::getTransaction).toList());
        }
    }

    /**
     * sets the tag id and the matching keyword id in the given record (changes are not stored in the DB) if no tag matches, then the
     * default tag is assigned
     *
     * @param userId      the user to select the tags from
     * @param transaction the transaction to modify
     */
    public void map(DSLContext db, UUID userId, CompleteTransactionEntity transaction) {
        LOGGER.log(Level.INFO, "mapping tags to transaction for {0}", new Object[]{userId});
        Map<TagRecord, List<KeywordRecord>> tags = tagProvider.selectTagsWithKeywordsByUserId(db, userId);
        map(transaction, tags, getDefaultTag(tags.keySet()));
        LOGGER.log(Level.INFO, "successfully mapped tags to transaction for {0}", new Object[]{userId});
    }

    /**
     * sets the tag id and the matching keyword id in the given record (changes are not stored in the DB) if no tag matches, then the
     * default tag is assigned
     *
     * @param transaction the transaction to map
     * @param tags        the tags to map the transaction to
     * @param defaultTag  the default tag
     */
    public void map(CompleteTransactionEntity transaction, Map<TagRecord, List<KeywordRecord>> tags, TagRecord defaultTag) {
        boolean found = false;
        for (Entry<TagRecord, List<KeywordRecord>> tagEntry : tags.entrySet()) {
            KeywordRecord keyword = findMatchingKeyword(transaction.getTransaction(), tagEntry.getValue());
            if (keyword != null) {
                if (found) {
                    transaction.addTagDuplicate(new TransactionTagDuplicateEntity(
                        transaction.getTransaction().getId(),
                        tagEntry.getKey(),
                        keyword
                    ));
                } else {
                    transaction.getTransaction().setTagId(tagEntry.getKey().getId());
                    transaction.getTransaction().setMatchingKeywordId(keyword.getId());
                    transaction.getTransaction().setNeedUserAttention(false);
                    transaction.setTag(tagEntry.getKey());
                    transaction.setMatchingKeyword(keyword);
                    found = true;
                }
            }
        }

        if (!found) {
            transaction.getTagDuplicates().clear();
            transaction.getTransaction().setTagId(defaultTag.getId());
            transaction.getTransaction().setNeedUserAttention(true);
            transaction.setTag(defaultTag);
        }
    }

    public void handleKeywordsAdded(DSLContext db, UUID userId, UUID tagId, List<KeywordRecord> keywords) {
        LOGGER.log(Level.INFO, "handling {0} added keywords for user {1}", new Object[]{keywords.size(), userId});

        for (KeywordRecord addedKeyword : keywords) {
            List<TransactionIdWithTagDuplicateCount> transactions = transactionProvider.selectTransactionIdsByMatchingKeyword(db, userId,
                addedKeyword.getKeyword());

            LOGGER.log(Level.INFO, "updating {0} transaction with new keyword", new Object[]{transactions.size()});
            for (TransactionIdWithTagDuplicateCount transaction : transactions) {
                if (transaction.alreadyMappedToAnyTag()) {
                    transactionTagDuplicateProvider.insertDuplicatedTag(db, transaction.transactionId(), tagId, addedKeyword.getId());
                    // transaction now has duplicates -> needs user attention
                    transactionProvider.updateTransactionNeedsUserAttention(db, transaction.transactionId(), true);
                } else {
                    transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(db, transaction.transactionId(), tagId,
                        addedKeyword.getId());
                }
            }
        }
    }

    /**
     * searches the first default tag in the given list
     *
     * @param tags a list of TagRecords
     * @return the first default tag
     */
    public TagRecord getDefaultTag(Collection<TagRecord> tags) {
        return tags.stream()
            .filter(TagRecord::getDefaultTag)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("tag", "default"));
    }

    protected KeywordRecord findMatchingKeyword(TransactionRecord transaction, List<KeywordRecord> keywords) {
        for (KeywordRecord keyword : keywords) {
            if (transaction.getReceiver().toLowerCase(Locale.ROOT).contains(keyword.getKeyword())) {
                return keyword;
            }
        }
        return null;
    }
}
