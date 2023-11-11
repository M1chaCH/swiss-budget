package ch.michu.tech.swissbudget.app.service;


import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.exception.KeywordAlreadyExistsException;
import ch.michu.tech.swissbudget.app.provider.KeywordProvider;
import ch.michu.tech.swissbudget.app.provider.TagProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider.TransactionIdWithTagDuplicateCount;
import ch.michu.tech.swissbudget.app.provider.TransactionTagDuplicateProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;
import org.jooq.exception.TooManyRowsException;

@ApplicationScoped
public class TagService {

    private final Provider<RequestSupport> supportProvider;
    private final TransactionProvider transactionProvider;
    private final TransactionTagDuplicateProvider transactionTagDuplicateProvider;
    private final TagProvider tagProvider;
    private final KeywordProvider keywordProvider;

    @Inject
    public TagService(Provider<RequestSupport> supportProvider, TransactionProvider transactionProvider,
        TransactionTagDuplicateProvider transactionTagDuplicateProvider, TagProvider tagProvider,
        KeywordProvider keywordProvider) {
        this.supportProvider = supportProvider;
        this.transactionProvider = transactionProvider;
        this.transactionTagDuplicateProvider = transactionTagDuplicateProvider;
        this.tagProvider = tagProvider;
        this.keywordProvider = keywordProvider;
    }

    public List<TagDto> getTags() {
        return tagProvider.selectTagsWithKeywordsByUserIdAsDto(supportProvider.get().getUserIdOrThrow());
    }

    public void validateNewKeyword(String keyword) {
        try {
            KeywordProvider.KeywordWithTagEntity existingKeyword = this.keywordProvider.selectByKeywordWithTagName(
                supportProvider.get().getUserIdOrThrow(), keyword);
            if (existingKeyword != null) {
                throw new KeywordAlreadyExistsException(existingKeyword.tagName(), existingKeyword.keyword());
            }
        } catch (TooManyRowsException e) {
            throw new KeywordAlreadyExistsException("(multiple...)", keyword);
        }
    }

    public void resolveConflict(String transactionId, int selectedTagId, int matchingKeywordId, boolean removeOthers) {
        RequestSupport support = supportProvider.get();
        String userId = support.getUserIdOrThrow();

        if (!transactionProvider.fetchExists(userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(userId, selectedTagId)) {
            throw new ResourceNotFoundException("tag", "" + selectedTagId);
        }
        if (!keywordProvider.fetchExists(userId, matchingKeywordId)) {
            throw new ResourceNotFoundException("keyword", "" + matchingKeywordId);
        }

        CompleteTransactionEntity transaction = transactionProvider.selectCompleteTransaction(userId, transactionId);

        if (removeOthers) {
            // delete keywords from duplicates that are not the newly selected
            transaction.getTagDuplicates().forEach(duplicate -> {
                if (duplicate.getMatchingKeyword().getId() != matchingKeywordId) {
                    duplicate.getMatchingKeyword().delete();
                }
            });

            // if old matching keyword is not in selected tag -> delete this keyword
            if (transaction.getTransaction().getMatchingKeywordId() != matchingKeywordId) {
                transaction.getMatchingKeyword().delete();
            }
        }

        transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transactionId, selectedTagId, matchingKeywordId);
        transactionProvider.deleteAllTagDuplicates(transactionId);
    }

    public void assignTag(String transactionId, int tagId, String keyword) {
        RequestSupport support = supportProvider.get();
        String userId = support.getUserIdOrThrow();

        if (!transactionProvider.fetchExists(userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(userId, tagId)) {
            throw new ResourceNotFoundException("tag", "" + tagId);
        }

        if (keyword != null) {
            this.addKeyword(support, tagId, keyword);
        } else {
            transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transactionId, tagId);
        }
    }

    public void changeTag(String transactionId, int tagId) {
        String userId = supportProvider.get().getUserIdOrThrow();

        if (!transactionProvider.fetchExists(userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(userId, tagId)) {
            throw new ResourceNotFoundException("tag", "" + tagId);
        }

        transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transactionId, tagId);
    }

    private void addKeyword(RequestSupport support, int tagId, String keyword) {
        support.logInfo(this, "adding keyword to tag: %s->%s", tagId, keyword);
        String userId = support.getUserIdOrThrow();
        int newKeywordId = keywordProvider.insertKeywordToTag(userId, tagId, keyword);

        List<TransactionIdWithTagDuplicateCount> transactions = transactionProvider.selectTransactionIdsByMatchingKeyword(userId, keyword);
        support.logInfo(this, "updating %s transactions with new keyword", transactions.size());
        for (TransactionIdWithTagDuplicateCount transaction : transactions) {
            if (transaction.alreadyMappedToTag()) { // TODO maybe faster if wrapped in transaction
                transactionTagDuplicateProvider.insertDuplicatedTag(transaction.transactionId(), tagId, newKeywordId);
                // transaction now has duplicates -> needs user attention
                transactionProvider.updateTransactionNeedsUserAttention(transaction.transactionId(), true);
            } else {
                transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transaction.transactionId(), tagId, newKeywordId);
            }
        }
    }
}
